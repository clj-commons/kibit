(ns jonase.kibit.core
  "Kibit's core functionality uses core.logic to suggest idiomatic
   replacements for patterns of code."
  (:refer-clojure :exclude [==])
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [jonase.kibit.rules :as core-rules])
  (:use [clojure.core.logic :only [prep run* defne project fresh membero ==]])
  (:import [clojure.lang LineNumberingPushbackReader]))

(def all-rules (map prep core-rules/all-rules))

(defne check-guards [expr guards]
  ([_ ()])
  ([_ [guard . rest]]
     (project [guard]
       (guard expr))
     (check-guards expr rest)))

(defn simplify-one
  ([expr]
    (simplify-one expr all-rules))
  ([expr rules]
    (first (run* [q]
             (fresh [pat guards alt]
               (membero [pat guards alt] rules)
               (== pat expr)
               (check-guards expr guards)
               (== q {:expr expr
                      :alt alt
                      :line (-> expr meta :line)}))))))

(declare expr-seq)
(defn simplify
  ([expr]
    (simplify expr all-rules))
  ([expr rules]
    (walk/postwalk identity (expr-seq expr))))

;; Reading source files
;; --------------------

;; `read-ns` is intended to be used with a  Clojure source file,
;; read in by a LineNumberingPushbackReader.  Expressions are
;; extracted using the clojure reader (ala `read`).
;; Line numbers are added as `:line` metadata to the forms.
(defn read-ns
  "Generate a lazy sequence of top level forms from a
  LineNumberingPushbackReader"
  [^LineNumberingPushbackReader r]
  (lazy-seq
   (let [form (read r false ::eof)]
     (when-not (= form ::eof)
       (cons form (read-ns r))))))

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

(defn check-file
  "Checks every expression (including sub-expressions) in a clojure
  source file against the rules and returns a lazy sequence of the
  result of unification"
  ([reader]
     (check-file reader all-rules))
  ([reader rules]
     (keep #(simplify % rules)
           (mapcat expr-seq (read-ns (LineNumberingPushbackReader. reader))))))
