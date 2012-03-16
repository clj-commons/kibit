(ns jonase.kibit.core
  "Kibit's core functionality uses core.logic to suggest idiomatic
   replacements for patterns of code."
  (:require [clojure.core.logic :as logic]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [jonase.kibit.rules :as core-rules])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; ### Important notes
;; Feel free to contribute rules to [kibit's github repo](https://github.com/jonase/kibit)

;; The rule sets
;; -------------
;;
;; Rule sets are stored in individual files that have a top level
;; `(def rules '{...})`.  The collection of rules are in the `rules`
;; directory.
;;
;; For more information, see: [rule](#jonase.kibit.rules) namespace
(def all-rules (map logic/prep core-rules/all-rules))

;; Building an alternative form
;; ----------------------------
;;
;; ### Unification
;; `unify` takes an expression and a `rule` pair (pattern and substitution).
;; For more information on rule pairs,
;; see: [rules](#jonase.kibit.rules) namespace
;;
;; If the expression-under-analysis matches the pattern, the substitution
;; expression is used to build an alternative expression. For example,
;; given the expression `(+ (f x) 1)` and the rule `[(+ ?x 1) (inc ?x)]`,
;; the expression `(inc (f x))` is built. This is all handled by `core.logic`.

;; (More docs on use of `run*` and `==` to come)
;;
;; Finally, if unification succeeds, a map containing the original
;; expression (`:expr`), the line where it appeared in the source file
;; (`:line`), the rule which was used (`:rule`) and the suggested
;; alternative built by `core.logic` (`:alt`) is returned. If the
;; unification failed `nil` is returned.
(defn unify
  "Unify expr with a rule pair. On success, return a map keyed with
  `:rule, :expr, :line and :alt`, otherwise return `nil`"
  [expr rules]
  (let [alt (first (logic/run* [q]
                     (logic/fresh [pat alt]
                       (logic/membero [pat alt] rules)
                       (logic/== expr pat)
                       (logic/== q alt))))]
    (when alt
      {:expr expr
       ;:rule rule
       :alt (if (seq? alt)
              (seq alt)
              alt)
       :line (-> expr meta :line)})))

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
     (keep #(unify % rules)
           (mapcat expr-seq (read-ns (LineNumberingPushbackReader. reader))))))
