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

(defn check-form
  ([expr]
     (check-form expr all-rules))
  ([expr rules]
     (when-let [alt (some (fn [[rule alt]] (and (sequential? expr)
                                                (logic/unifier expr rule)
                                                alt))
                          rules)]
       {:expr expr
        :alt alt
        :line (-> expr meta :line)})))

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
