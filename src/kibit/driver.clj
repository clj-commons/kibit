(ns kibit.driver
  (:require [clojure.java.io :as io]
            [kibit.rules :refer [all-rules]]
            [kibit.check :refer [check-file]]
            [kibit.replace :refer [replace-file]]
            [kibit.reporters :refer :all]
            [clojure.tools.cli :refer [cli]])
  (:import [java.io File]))

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

(defn- run-replace [source-files rules options]
  (doseq [file source-files]
    (replace-file file
                  :rules (or rules all-rules)
                  :interactive (:interactive options))))

(defn- run-check [source-files rules {:keys [reporter]}]
  (mapcat (fn [file] (try (check-file file
                                      :reporter (name-to-reporter reporter
                                                                  cli-reporter)
                                      :rules (or rules all-rules))
                          (catch Exception e
                            (binding [*out* *err*]
                              (println "Check failed -- skipping rest of file")
                              (println (.getMessage e))))))
          source-files))

(defn run [source-paths rules & args]
  (let [[options file-args usage-text] (apply (partial cli args) cli-specs)
        source-files (mapcat #(-> % io/file find-clojure-sources-in-dir)
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
