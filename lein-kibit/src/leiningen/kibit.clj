(ns leiningen.kibit
  (:require [leiningen.core.eval :refer [eval-in-project]]
            [clojure.tools.namespace.find :refer [find-namespaces]]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.nio.file Paths)))


(defn ^:no-project-needed kibit
  [project & args]
  (let [src-paths     (get-in project [:kibit :source-paths] ["rules"])
        repositories (:repositories project)
        local-repo    (:local-repo project)
        kibit-project `{:dependencies [[jonase/kibit ~(str/trim-newline
                                                        (slurp
                                                          (io/resource
                                                            "jonase/kibit/VERSION")))]]
                        :source-paths ~src-paths
                        :repositories ~repositories
                        :local-repo   ~local-repo}
        cwd           (.toAbsolutePath (Paths/get "" (into-array String nil)))
        ;; This could become a transducer once we want to force a dependency on Lein 1.6.0 or higher.
        paths         (->> (concat                          ;; Collect all of the possible places sources can be defined.
                             (:source-paths project)
                             [(:source-path project)]
                             (mapcat :source-paths (get-in project [:profiles]))
                             (mapcat :source-paths (get-in project [:cljsbuild :builds]))
                             (mapcat :source-paths (get-in project [:cljx :builds])))
                           (filter some?)                   ;; Remove nils
                           ;; Convert all String paths to absolute paths (Leiningen turns root :source-paths into absolute path).
                           (map #(.toAbsolutePath (Paths/get % (into-array String nil))))
                           (map #(.relativize cwd %))       ;; Relativize them them all to make them easier on the eyes.
                           (map str)                        ;; Convert them back to strings.
                           (set))                           ;; Deduplicate paths by putting them all in a set.
        rules         (get-in project [:kibit :rules])
        src           `(kibit.driver/external-run '~paths
                                                  (when ~rules
                                                    (apply concat (vals ~rules)))
                                                  ~@args)
        ns-xs         (mapcat identity (map #(find-namespaces [(io/file %)]) src-paths))
        req           `(do (require 'kibit.driver)
                           (doseq [n# '~ns-xs]
                             (require n#)))]
    (try (eval-in-project kibit-project src req)
         (catch Exception e
           (throw (ex-info "" {:exit-code 1}))))))
