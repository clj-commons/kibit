(ns jonase.kibit.core
  "Kibit's core functionality uses core.logic to suggest idiomatic
   replacements for patterns of code."
  (:refer-clojure :exclude [==])
  (:require [clojure.java.io :as io]
            [clojure.walk :as walk]
            [clojure.core.logic :as logic]
            [jonase.kibit.rules :as core-rules]
            [jonase.kibit.reporters :as reporters])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; ### Important notes
;; Feel free to contribute rules to [kibit's github repo](https://github.com/jonase/kibit)

;; The rule sets
;; -------------
;;
;; Rule sets are stored in individual files that have a top level
;; `(defrules rules ...)`. The collection of rules are in the `rules`
;; directory.
;;
;; Here, we logically prepare all the rules, by substituting in logic vars
;; where necessary.
;;
;; For more information, see: [rules](#jonase.kibit.rules) namespace
(def all-rules (map logic/prep core-rules/all-rules))

;; Building an alternative form
;; ----------------------------
;;
;; ### Applying unification

(logic/defne check-guards [expr guards]
  ([_ ()])
  ([_ [guard-fn . rest]]
     (logic/project [guard-fn]
       (guard-fn expr))
     (check-guards expr rest)))

(defn simplify-one
  ([expr]
    (simplify-one expr all-rules))
  ([expr rules]
    (first (logic/run* [q]
             (logic/fresh [pat guards alt]
               (logic/membero [pat guards alt] rules)
               (logic/== pat expr)
               (check-guards expr guards)
               (logic/== q {:expr expr
                            :alt alt
                            :line (-> expr meta :line)}))))))

;; This walks across all the forms within an expression,
;; checking each inner form and returning a full simplify-map.
;; (Even if the forms are the same.)
;;
;; We build the simplify-map at the end because
;; Clojure 1.3 munges the metadata in transients (so also in clojure.walk).
(defn simplify
  ([expr]
    (simplify expr all-rules))
  ([expr rules]
    (let [line-num (-> expr meta :line)
          simp-partial #(simplify-one %1 rules)
          alt (walk/postwalk #(or (-> % simp-partial :alt) %) expr)]
      {:expr expr
       :alt alt
       :line line-num})))

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
    (try
      (let [form (read r false ::eof)]
        (when-not (= form ::eof)
          (cons form (read-file r))))
      (catch Exception e
        (println "A form was skipped because it relies on active parsing/evaluating - issue #14")))))

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

;; The reader is converted into a LineNumberingPushbackReader.
;; Optional rule sets and verbosity can be set with keyword args,
;; :rules, :verbose -
;;
;; `(check-via-reader my-reader :rules rule-set :verbose true)`
;;
;; Verbosity will report all the sub-expression substituions individually,
;; in addition to reporting them all on the top-level form.  This is an
;; an ideal way to carefully inspect your code.
(defn check-via-reader
  "Simplifies every expression (including sub-expressions) read in from
  a reader and returns a lazy sequence of the result of unification
  (`simplify` function)."
  ([reader & kw-opts]
    (let [{:keys [rules verbose]
           :or {rules   all-rules,
                verbose false}} (apply hash-map kw-opts)]
      (if verbose
        (keep #(simplify-one % rules)
              (mapcat expr-seq (read-file (LineNumberingPushbackReader. reader))))
        (keep  #(simplify % rules) (read-file (LineNumberingPushbackReader. reader)))))))

;; The results from simplify get passed to a `reporter`.
;; A reporter can be any function that expects a single map.
;; TODO - talk about optional args
;;
;; The entire sequence of simplify-maps from a given `source-file`
;; are processed with `doseq`, since reporting is side-effect action.
;;
;; For more information on reporters, see: [reporters](#jonase.kibit.reporters) namespace
(defn check-file
  "Checks every expression (including sub-expressions) in a clojure
  source file against the rules and processes them with a reporter"
  ([source-file & kw-opts]
    (let [{:keys [rules verbose reporter]
           :or {rules     all-rules
                verbose   false
                reporter  reporters/cli-reporter}} (apply hash-map kw-opts)]
      (with-open [reader (io/reader source-file)]
        (doseq [simplify-map (check-via-reader
                            reader :rules rules :verbose verbose)]
          (reporter simplify-map))))))

