(ns jonase.kibit.core
  (:require [clojure.core.logic :as logic]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [jonase.kibit.arithmetic :as arith]
            [jonase.kibit.control-structures :as control]
            [jonase.kibit.misc :as misc])
  (:import [clojure.lang LineNumberingPushbackReader]))

(def all-rules (merge control/rules
                      arith/rules
                      misc/rules))

(defn read-ns [r]
  (lazy-seq
   (let [form (read r false ::eof)
         line-num (.getLineNumber r)]
     (when-not (= form ::eof)
       (cons (with-meta form {:line line-num}) (read-ns r))))))

(defn unify [expr rule]
  (let [[r s] (#'logic/prep rule)
        alt (first (logic/run* [alt]
                     (logic/== expr r)
                     (logic/== s alt)))]
    (when alt
      {:expr expr
       :rule rule
       :alt (seq alt)
       :line (-> expr meta :line)})))

(defn check-form
  ([expr]
     (check-form expr all-rules))
  ([expr rules]
     (when (sequential? expr)
       (loop [expr expr
              alt-map nil]
          (if-let [new-alt-map (some #(unify expr %) rules)]
            (recur (:alt new-alt-map)
                   new-alt-map)
            alt-map)))))

(defn expr-seq [expr]
  (tree-seq sequential?
            seq
            expr))

(defn check-file
  ([reader]
     (check-file reader all-rules))
  ([reader rules]
     (keep check-form
           (mapcat expr-seq (read-ns (LineNumberingPushbackReader. reader))))))

