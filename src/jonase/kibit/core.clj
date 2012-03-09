(ns jonase.kibit.core
  "Kibit's core functionality uses core.logic to suggest idiomatic
   replacements for patterns of code."
  (:require [clojure.core.logic :as logic]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [jonase.kibit.rules :as core-rules])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; The rule sets
;; -------------
;;
;; Rule sets are stored in individual files that have a top level
;; `(def rules '{...})`.  The collection of rules are in the `rules`
;; directory.
;;
;; For more information, see: [rule](#jonase.kibit.rules) namespace
(def all-rules core-rules/all-rules)

;; Unification
;; -----------
;;
;; `unify` takes an expression and a `rule`. A rule is a pair
;; consisting of
;;
;; * a pattern expression (e.g. `(+ ?x 1)`)
;; * a substitution expression (e.g. `(inc ?x)`
;;
;; If the pattern matches the expression the substitution expression
;; is used to build an alternative expression. For example, given the
;; expression `(+ (f x) 1)` and the rule `[(+ ?x 1) (inc ?x)]`, the
;; expression `(inc (f x))` is built. This is all handled by
;; `core.logic`.
;;
;; Finally, if unification succeeds, a map containing the original
;; expression (`:expr`), the line where it appeared in the source file
;; (`:line`), the rule which was used (`rule`) and the suggested
;; alternative built by `core.logic` (`:alt`) is returned. If the
;; unification failed `nil` is returned.

(defn unify
  "Attempts to unify expr with rule. On success a map is returned
  containing :rule, :expression, :line and :alt (suggested
  alternative) keys. Returns nil if unification fails"
  [expr rule]
  (let [[r s] (#'logic/prep rule)
        alt (first (logic/run* [alt]
                     (logic/== expr r)
                     (logic/== s alt)))]
    (when alt
      {:expr expr
       :rule rule
       :alt (if (seq? alt)
              (seq alt)
              alt)
       :line (-> expr meta :line)})))

;; The `check-form` function does a linear search over the rules and
;; returns the map created by the first successful unification with
;; expr.
(defn check-form
  "Returns the first successful unification for expr against the
  rules. Returns nil if no rule unifies with expr"
  ([expr]
     (check-form expr all-rules))
  ([expr rules]
     (when (sequential? expr)
       (some #(unify expr %) rules))))

;; This walks across all the forms within a seq'd form/expression,
;; checking each inner form. We have to restore `:expr` because it
;; gets munged in the tree/expr walk
(defn check-expr
  "Given a full expression/form-of-forms/form, a map containing the
  alternative suggestion info, or `nil`"
  [expr]
  (if-let [new-expr (walk/walk #(or (-> % check-form :alt) %) check-form expr)]
    (assoc new-expr :expr expr)
    nil))

;; Reading source files
;; --------------------
;;
;; `read-ns` reads a Clojure source file and returns a sequence of the
;; top level forms. Line numbers are added as `:line` metadata to the
;; forms.
(defn read-ns
  "Generate a lazy sequence of top level forms from a
  LineNumberingPushbackReader"
  [r]
  (lazy-seq
   (let [form (read r false ::eof)
         line-num (.getLineNumber r)]
     (when-not (= form ::eof)
       (cons (with-meta form {:line line-num}) (read-ns r))))))

;; `Expr-seq` takes an expression and returns a lazy sequence of the
;; expression itself and all its sub-expressions in a depth-first
;; manner:
;;
;;    user=> (expr-seq '(if (pred? x) (inc x) x))
;;    ((if (pred? x) (inc x) x)
;;     if
;;     (pred? x)
;;     pred?
;;     x
;;     (inc x)
;;     inc
;;     x
;;     x)
(defn expr-seq
  "Returns a lazy (depth-first) sequence of expr and all its
  sub-expressions"
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
     (keep check-form
           (mapcat expr-seq (read-ns (LineNumberingPushbackReader. reader))))))
