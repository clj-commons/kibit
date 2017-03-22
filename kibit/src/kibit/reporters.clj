(ns kibit.reporters
  "Format and display output generated from check-* functions"
  (:require [clojure.string :as string]
            [clojure.pprint :as pp])
  (:import [java.io StringWriter]))

;; Reporters are used with `check-file`, passed in with the `:reporter`
;; keyword argument.  For more information, see the [check](#kibit.check)
;; namespace.
;;
;; There is no limit to a reporter - Clojure Data, JSON, HTML...
;;
;; Here we have supplied a reporter for standard-out.

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

(defn cli-reporter
  "Print a check-map to `*out*` in plain text."
  [check-map]
  (let [{:keys [file line expr alt]} check-map]
    (printf "At %s:%s:\nConsider using:\n" file line)
    (pprint-code alt)
    (println "instead of:")
    (pprint-code expr)
    (newline)))

(defn gfm-reporter
  "Print a check-map to `*out*` in github flavored markdown."
  [check-map]
  (let [{:keys [file line expr alt]} check-map]
    (printf "----\n##### `%s:%s`\nConsider using:\n" file line)
    (println "```clojure")
    (pprint-code alt)
    (println "```")
    (println "instead of:")
    (println "```clojure")
    (pprint-code expr)
    (println "```")
    (newline)))

(def name-to-reporter {"markdown" gfm-reporter
                       "text" cli-reporter})
