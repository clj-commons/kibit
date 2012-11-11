(ns kibit.driver
  (:require [clojure.tools.namespace :refer [find-clojure-sources-in-dir]]
            [clojure.java.io :as io]
            [kibit.check :refer [check-file]]
            [kibit.reporters :refer [cli-reporter]]))

(defn run [project & args]
  (let [source-files (if (empty? args)
                       (mapcat #(-> % io/file find-clojure-sources-in-dir)
                               (or (:source-paths project) [(:source-path project)]))
                       args)]
    (doseq [file source-files]
      (try (->> (check-file file)
                (map cli-reporter)
                doall)
           (catch Exception e
             (println "Check failed -- skipping rest of file")
             (println (.getMessage e)))))))