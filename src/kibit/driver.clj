(ns kibit.driver
  (:require [clojure.java.io :as io]
            [kibit.rules :refer :all]
            [kibit.check :refer [check-file]]
            [kibit.reporters :refer :all]
            [clojure.tools.cli :refer [cli]])
  (:import [java.io File]))

(def cli-specs [["-r" "--reporter"
                 "The reporter used when rendering suggestions"
                 :default "text"]])

(defn ends-with?
  "Returns true if the java.io.File ends in any of the strings in coll"
  [file coll]
  (some #(.endsWith (.getName file) %) coll))

(defn clojure-file?
  "Returns true if the java.io.File represents a Clojure source file.
  Extensions taken from https://github.com/github/linguist/blob/master/lib/linguist/languages.yml"
  [file]
  (and (.isFile file)
       (ends-with? file [".clj" ".cl2" ".cljc" ".cljs" ".cljscm" ".cljx" ".hic" ".hl"])))

(defn find-clojure-sources-in-dir
  "Searches recursively under dir for Clojure source files.
  Returns a sequence of File objects, in breadth-first sort order.
  Taken from clojure.tools.namespace.find"
  [^File dir]
  ;; Use sort by absolute path to get breadth-first search.
  (sort-by #(.getAbsolutePath ^File %)
           (filter clojure-file? (file-seq dir))))

(defn filtered-rules
  [rules excludes file]
  (let [file-entry (get excludes (.getName file))
        file-excludes (if (= :all file-entry)
                        #{:all}
                        (clojure.set/union (get excludes :all)
                                           file-entry))]
    (seq (cond (:all file-excludes) nil
               (= nil file-excludes) (or rules all-rules)
               :else (or rules
                         (rule-sets (clojure.set/difference (set (keys rule-map))
                                                            file-excludes)))))))

(defn run
  "Runs analysis on all files under each of the paths specified in source-paths.
  Accepts custom rules to be used in place of the default set of rules. Accepts
  excludes, a map of rules to exclude per file.

  excludes is specified with the following form:

  {\"file-name.clj\" #{:all}
  \"file-name2.cljx\" #{:rule1}
  :all #{:rule1 :rule2}}

  In excludes, :all may be used in place of either the file name or the rules in
  order to exclude a rule-set for all files or all rule-sets for a file. When
  using custom rules, :all may only be used to denote entire file exclusions
  (i.e. {\"file\" #{:all}})"
  [source-paths rules excludes & args]
  (let [[options file-args usage-text] (apply (partial cli args) cli-specs)
        source-files (mapcat #(-> % io/file find-clojure-sources-in-dir)
                             (if (empty? file-args) source-paths file-args))
        rule-filter-fn (partial filtered-rules rules excludes)]
    (mapcat (fn [file] (try (if-let [file-rules (rule-filter-fn file)]
                             (check-file file
                                         :reporter (name-to-reporter (:reporter options)
                                                                     cli-reporter)
                                         :rules file-rules)
                             (println "Skipping file: " (.getPath file)))
                           (catch Exception e
                             (binding [*out* *err*]
                               (println "Check failed -- skipping rest of file")
                               (println (.getMessage e))))))
            source-files)))

(defn external-run
  "Used by lein-kibit to count the results and exit with exit-code 1 if results are found"
  [source-paths rules excludes & args]
  (if (zero? (count (apply run source-paths rules excludes args)))
    (System/exit 0)
    (System/exit 1)))
