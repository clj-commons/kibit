(ns leiningen.kibit
  (:require [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [kibit.check :as kibit]
            [kibit.rules :as rules]
            [kibit.reporters :as reporters]))

(defn kibit
  "Suggest idiomatic replacements for patterns of code."
  [project & opts]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir) paths)
        kw-opts (apply hash-map (map read-string opts))
        verbose (or (:verbose kw-opts) false)
        check (if verbose kibit/check-subforms kibit/check-toplevel-forms)]
    (doseq [source-file source-files]
      (with-open [reader (io/reader source-file)]
        (printf "== %s ==\n" (or (second (clj-ns/read-file-ns-decl source-file)) source-file))
        (try
          (->> (check reader rules/all-rules)
               (filter #(contains? % :alt))
               (map reporters/cli-reporter)
               doall)
          (catch Exception e
            (println "Check failed -- skipping rest of file")
            (when verbose (.printStackTrace e))))))))

