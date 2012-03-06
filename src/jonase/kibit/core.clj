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

(defn broke-rule? [expr rules]
  (some (fn [rule] (and (sequential? expr)
                        (logic/unifier rule  [expr '?alt])
                        (vector rule (logic/unifier rule  [expr '?alt]))))
    rules))

(defn check-form
  "Return the single best suggestion for a given form"
  ([expr]
     (check-form expr all-rules))
  ([expr rules]
     (when-let [[[rule _ :as orig-rule][_ alt :as unified-rule]]
                (loop [ex expr
                       rules-vec nil]
                  (let [[[_ _][_ alt] :as rvec] (broke-rule? ex rules)]
                    (if (nil? alt)
                      (try rules-vec (catch Exception e nil))
                      (recur alt rvec))))]
       {:expr expr
        :alt alt
        :rule orig-rule
        :unified-rule unified-rule
        :line (-> expr meta :line)})))

(defn check
  "This is a presentation version of check-form,
  used to print broken-rules to stdout"
  ([expr]
   (check expr all-rules))
  ([expr rules]
   (let [{:keys [line alt expr]} (check-form expr rules)]
     (printf "[Kibit:%s] Consider %s instead of %s\n" line (reverse (into '() alt)) expr))))

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

