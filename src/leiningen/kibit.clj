(ns leiningen.kibit
  (:require [clojure.string :as string]
            [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [jonase.kibit.core :as kibit])
  (:import [java.io StringWriter]))

;; A hack to get the code indented. 
(defn pprint-code [form]
  (let [string-writer (StringWriter.)]
    (pp/write form
              :dispatch pp/code-dispatch
              :stream string-writer
              :pretty true)
    (->> (str string-writer)
         string/split-lines
         (map #(str "  " %))
         (string/join "\n")
         println)))


(defn kibit
  "Suggest idiomatic replacements for patterns of code."
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir)
                             paths)]
    (doseq [source-file source-files]
      (printf "== %s ==\n"
              (or (second (clj-ns/read-file-ns-decl source-file)) source-file))
      (with-open [reader (io/reader source-file)]
        (doseq [{:keys [line expr alt]} (kibit/check-file reader)]
          (printf "[%s] Consider:\n" line)
          (pprint-code alt)
          (println "instead of:")
          (pprint-code expr)
          (newline)))
      (flush))))
