(ns leiningen.kibit
  (:require [clojure.tools.namespace :as ns]
            [clojure.java.io :as io]
            [jonase.kibit.core :as kibit]))

(defn kibit [project]
  (let [namespaces (-> project
                       :source-path
                       io/file
                       ns/find-namespaces-in-dir)]
    (doseq [ns-sym namespaces]
      (try
        (println "==" ns-sym "==")
        (kibit/check-ns ns-sym)
        (catch RuntimeException e (println ns-sym "not found.")))
    (println "done."))))
