(ns leiningen.kibit
  (:require [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [jonase.kibit.core :as kibit]))

(defn kibit
  "Suggest idiomatic replacements for patterns of code."
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])]
    (doseq [path paths]
      (doseq [ns-sym (clj-ns/find-namespaces-in-dir (io/file path))]
        (try
          (println "==" ns-sym "==")
          (kibit/check-ns ns-sym path)
          (catch RuntimeException e (println ns-sym "not found.")))
        (println "done.")))))
