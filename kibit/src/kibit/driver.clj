(ns kibit.driver
  "The (leiningen) facing interface for Kibit. Provides helpers for finding files in a project, and
  linting a list of files."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.cli :refer [cli]]
            [kibit
             [check :refer [check-file]]
             [replace :refer [replace-file]]
             [reporters :refer :all]
             [rules :refer [all-rules]]])
  (:import java.io.File))

(def cli-specs [["-r" "--reporter"
                 "The reporter used when rendering suggestions"
                 :default "text"]
                ["-e" "--replace"
                 "Automatially apply suggestions to source file"
                 :flag true]
                ["-i" "--interactive"
                 "Interactively prompt before replacing suggestions in source file (Requires `--replace`)"
                 :flag true]])

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

(declare read-edn-file)

(defn- opts [^java.io.File cfg-file]
 {:readers
  {'include
    (fn [file]
      (let [f (io/file (.getParent cfg-file) file)]
        (if (.exists f)
          (read-edn-file f)
          (binding [*out* *err*]
            (println "WARNING: included file" (.getCanonicalPath f) "does not exist.")))))}})

(defn- read-edn-file [^java.io.File f]
  (try (edn/read-string (opts f) (slurp f))
    (catch Exception e
      (binding [*out* *err*]
        (println "WARNING: error while reading"
          (.getCanonicalPath f) (format "(%s)" (.getMessage e)))))))

(defn- config-dir
  ([] (config-dir (io/file (System/getProperty "user.dir"))))
  ([cwd]
    (loop [dir (io/file cwd)]
      (let [cfg-dir (io/file dir ".kibit")]
        (if (.exists cfg-dir)
          (if (.isDirectory cfg-dir)
            cfg-dir
            (throw (Exception. (str cfg-dir " must be a directory"))))
          (when-let [parent (.getParentFile dir)]
            (recur parent)))))))

(defn- get-config-map []
  (let [cfg-dir (config-dir (io/file (System/getProperty "user.dir")))]
    (let [f (io/file cfg-dir "config.edn")]
      (when (.exists f)
        (read-edn-file f)))))

(defn- run-replace [source-files rules options]
  (let [config-map (get-config-map)]
    (doseq [file source-files]
      (replace-file file
                    :rules (or rules all-rules)
                    :interactive (:interactive options)
                    :exclusions (:exclusions config-map)
                    :custom (:custom config-map)))))

(defn- run-check [source-files rules {:keys [reporter]}]
  (let [config-map (get-config-map)]
    (mapcat (fn [file] (try (check-file file
                                      :reporter (name-to-reporter reporter
                                                                  cli-reporter)
                                      :rules (or rules all-rules)
                                      :exclusions (:exclusions config-map)
                                      :custom (:custom config-map))
                          (catch Exception e
                            (let [e-info (ex-data e)]
                              (binding [*out* *err*]
                                (println (format "Check failed -- skipping rest of file (%s:%s:%s)"
                                                 (.getPath file)
                                                 (:line e-info)
                                                 (:column e-info)))
                                (println (.getMessage e)))))))
          source-files)))

(defn run [source-paths rules & args]
  "Runs the kibit checker against the given paths, rules and args.

  Paths is expected to be a sequence of io.File objects.

  Rules is either a collection of rules or nil. If rules is nil, all of kibit's checkers are used.

  Optionally accepts a :reporter keyword argument, defaulting to \"text\"
  If :replace is provided in options, suggested replacements will be performed automatically."
  (let [[options file-args usage-text] (apply (partial cli args) cli-specs)
        source-files                   (mapcat #(-> % io/file find-clojure-sources-in-dir)
                                               (if (empty? file-args) source-paths file-args))]
    (if (:replace options)
      (run-replace source-files rules options)
      (run-check source-files rules options))))

(defn external-run
  "Used by lein-kibit to count the results and exit with exit-code 1 if results are found"
  [source-paths rules & args]
  (if (zero? (count (apply run source-paths rules args)))
    (System/exit 0)
    (System/exit 1)))
