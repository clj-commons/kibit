(ns leiningen.kibit
  (:require [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [jonase.kibit.core :as kibit]))

(defn kibit
  "Suggest idiomatic replacements for patterns of code."
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        namespaces (apply concat (for [path paths]
                                   (clj-ns/find-namespaces-in-dir (io/file path))))]
    (doseq [ns-sym namespaces]
      (try
        (println "==" ns-sym "==")
        (kibit/check-ns ns-sym)
        (catch RuntimeException e (println ns-sym "not found."))))))
      
