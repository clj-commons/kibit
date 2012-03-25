(ns kibit.check
  (:require [kibit.core :as core])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; ### Important notes
;; Feel free to contribute rules to [kibit's github repo](https://github.com/jonase/kibit)

;; Reading source files
;; --------------------

;; `read-file` is intended to be used with a  Clojure source file,
;; read in by a LineNumberingPushbackReader.  Expressions are
;; extracted using the clojure reader (ala `read`).
;; Line numbers are added as `:line` metadata to the forms.
(defn read-file
  "Generate a lazy sequence of top level forms from a
  LineNumberingPushbackReader"
  [^LineNumberingPushbackReader r]
  (lazy-seq
   (let [form (read r false ::eof)]
     (when-not (= form ::eof)
       (cons form (read-file r))))))

;; `tree-seq` returns a lazy-seq of nodes for a tree.
;; Given an expression, we can then match rules against its pieces.
;; This is like using `clojure.walk` with `identity`:
;;
;;     user=> (expr-seq '(if (pred? x) (inc x) x))
;;     ((if (pred? x) (inc x) x)
;;      if
;;      (pred? x)
;;      pred?
;;      x
;;      (inc x)
;;      inc
;;      x
;;      x)`
;;
(defn expr-seq
  "Given an expression (any piece of Clojure data), return a lazy (depth-first)
  sequence of the expr and all its sub-expressions"
  [expr]
  (tree-seq sequential?
            seq
            expr))

(defn- build-result-map [expr simplified-expr]
  (merge {:expr expr
          :line (-> expr meta :line)}
         (when (not= expr simplified-expr)
           {:alt simplified-expr})))

(defn check-toplevel-forms [reader rules]
  (for [expr (read-file (LineNumberingPushbackReader. reader))
        :let [simplified-expr (core/simplify expr rules)]]
    (build-result-map expr simplified-expr)))

(defn check-subforms [reader rules]
  (for [expr (mapcat expr-seq (read-file (LineNumberingPushbackReader. reader)))
        :let [simplified-expr (core/simplify-one expr rules)]]
    (build-result-map expr simplified-expr)))
