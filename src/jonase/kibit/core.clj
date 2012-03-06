(ns jonase.kibit.core
  (:require [clojure.core.logic :as logic]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [jonase.kibit.arithmetic :as arith]
            [jonase.kibit.control-structures :as control]
            [jonase.kibit.misc :as misc]))

(def all-rules (merge control/rules
                      arith/rules
                      misc/rules))

(defn src [path]
  (if-let [res (io/resource path)]
    (clojure.lang.LineNumberingPushbackReader. (io/reader res))
    (throw (RuntimeException. (str "File not found: " path)))))

(defn source-file [ns-sym]
  (-> (name ns-sym)
      (string/replace "." "/")
      (string/replace "-" "_")
      (str ".clj")))

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
   (for [rule rules
         ; See if a rule is broken, if it is, show a unified alternative
         :let [[_ alt :as unified-rule]  (logic/unifier rule  [expr '?alt])
               rule-broke (not (nil?
                                 (and (sequential? expr)
                                       unified-rule)))]
         :when rule-broke]
       {:expr expr
        :alt alt
        :rule rule
        :unified-rule unified-rule
        :line (-> expr meta :line)})))

(defn check
  "This is a presentation version of check-form,
  used to print broken-rules to stdout"
  ([expr]
   (check expr all-rules))
  ([expr rules]
   (doseq [{:keys [line alt expr]} (check-form expr rules)]
     (printf "[Kibit:%s] Consider %s instead of %s\n" line alt expr))))

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
