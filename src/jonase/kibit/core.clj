(ns jonase.kibit.core
  (:require [clojure.core.logic :as logic]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [jonase.kibit.arithmetic :as arith]
            [jonase.kibit.control-structures :as control]
            [jonase.kibit.misc :as misc])
  (:import [java.io PushbackReader]))

(def all-rules (merge control/rules
                      arith/rules
                      misc/rules))

(defn src [path]
  (if-let [res (io/resource path)]
    (PushbackReader. (io/reader res))
    (throw (RuntimeException. (str "File not found: " path)))))

(defn source-file [ns-sym]
  (-> (name ns-sym)
      (string/replace "." "/")
      (string/replace "-" "_")
      (str ".clj")))

(defn read-ns [r]
  (lazy-seq
   (let [form (read r false ::eof)]
     (when-not (= form ::eof)
       (cons form (read-ns r))))))

(defn check [expr rules]
  (doseq [[rule alt] rules]
    (when (and (sequential? expr)
               (logic/unifier expr rule))
      (println "[Kibit] Consider" alt "instead of" expr))))

(defn expr-seq [expr]
  (tree-seq sequential?
            seq
            expr))

(defn check-ns
  ([ns-sym rules]
     (with-open [reader (-> ns-sym source-file src)]
       (doseq [form (mapcat expr-seq (read-ns reader))]
         (check form rules))))
  ([ns-sym]
     (check-ns ns-sym all-rules)))
