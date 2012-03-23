(ns leiningen.kibit
  (:require [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [kibit.core :as kibit]))

(defn kibit
  "Suggest idiomatic replacements for patterns of code."
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir)
                             paths)]
    (doseq [source-file source-files]
      (printf "== %s ==\n"
              (or (second (clj-ns/read-file-ns-decl source-file)) source-file))
      (kibit/check-file source-file)
      (flush))))
