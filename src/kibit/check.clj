(ns kibit.check
  "Kibit's integration point and public API"
  (:require [clojure.java.io :as io]
            [clojure.core.logic :as logic]
            [kibit.core :as core]
            [kibit.rules :as core-rules]
            [kibit.reporters :as reporters])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; ### Overview
;; The public API for Kibit is through the `check-*` functions below.
;;
;;  * `check-expr` - for checking single expressions (great on the REPL)
;;  * `check-reader` - for checking all forms passed in via a `PushbackReader`
;;  * `check-file` - for checking any file, or string/URI/URL for a file
;;
;; All other functions in this namespace exist to provide support and ease
;; of use for integrated Kibit into other technologies.

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
;; For more information, see: [rules](#kibit.rules) namespace
(def all-rules (map logic/prep core-rules/all-rules))

;; Reading source files
;; --------------------
;;
;; TODO Paul - give an overview here

;; ### Handling read errors
;; TODO Paul - explain this more
(def ^:dynamic *check-error-handler*
  (fn [ex reader]
    (throw (Exception.
             (str "Kibit's reader crashed; see kibit.check/read-file:"
                  (.getMessage ex))
             ex))))

(defn ignore-error-handler
  "This is an error handler that will silently skip over forms
  that cause errors - a work in progress"
  [ex reader] ; TODO We're going to have to (.skip reader n), where n is the number of chars to skip until the next form
  nil)

;; ### Extracting forms

;; `read-file` is intended to be used with a Clojure source file,
;; read in by Clojure's LineNumberingPushbackReader *(LNPR)*. Expressions are
;; extracted using the clojure reader (ala `read`), and line numbers
;; are added as `:line` metadata to the forms (via LNPR).
;; Exceptions are handled using a dynamic handler, `*check-error-handler*`
;;
;; See above for more details on how to control error handling
(defn read-file
  "Generate a lazy sequence of top level forms from a
  LineNumberingPushbackReader"
  [^LineNumberingPushbackReader r]
  (lazy-seq
   (let [form (try
                (read r false ::eof)
                (catch Exception e
                  (*check-error-handler* e r)))]
     (when-not (= form ::eof)
       (cons form (read-file r))))))

;; ### Analyzing the pieces

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
;; This is needed for `:subform` reporting.
(defn expr-seq
  "Given an expression (any piece of Clojure data), return a lazy (depth-first)
  sequence of the expr and all its sub-expressions"
  [expr]
  (tree-seq sequential?
            seq
            expr))

;; Building results / `simplify-maps`
;; -----------------------------------

;; See the [core](#kibit-core) namespace for details in simplifying an expression.
(defn- build-simplify-map
  "Construct the canonical simplify-map
  given an expression and a simplified expression."
  [expr simplified-expr]
  {:expr expr
   :line (-> expr meta :line)
   :alt simplified-expr})

;; ### Guarding the check

;; Guarding `check-*` allows for fine-grained control over what
;; gets passed to a reporter.  This allows those using kibit
;; as a library or building out tool/IDE integration to shape
;; the results prior to reporting.
;;
;; Normally, you'll only want to report an alternative form if it differs
;; from the original expression form.  You can use `identity` to short circuit
;; the guard and ALWAYS receive the `simlify-map`.
;;
;; Check-guards take a map and return a map or nil

(defn unique-alt?
  "A 'check guard' that only returns a result if the
  alternative is different than the original expresion"
  [simplify-map]
  (let [{:keys [expr alt]} simplify-map]
    (when-not (= alt expr)
      simplify-map)))

;; Default args for the keyword options passed to the check-* functions
(def ^:private default-args
  {:rules      all-rules
   :guard      unique-alt?
   :resolution :subform})

;; ### Resolution
;; Kibit can report at various levels of resolution.
;;
;; `:toplevel` will simplify a toplevel form, like `(defn ...)`
;; and all of the subforms it contains. This is exceptionally useful if
;; you're looking for paragraph-sized suggestions, or you're using
;; Kibit on the REPL (per expression).
;;
;; `:subform` will only report on the subforms. This is most common
;; for standard reporting, and what gets used when Kibit's Leinigen
;; plugin is `:verbose false`

;; Map the levels of resolution to the correct combination of `simplify`
;; and `read-seq` functions.
(def ^:private res->simplify
  {:toplevel core/simplify
   :subform  core/simplify-one})

(def ^:private res->read-seq
  {:toplevel (fn [reader] (read-file (LineNumberingPushbackReader. reader)))
   :subform  (fn [reader] (mapcat expr-seq (read-file (LineNumberingPushbackReader. reader))))})

;; Checking the expressions
;; ------------------------
;;
;; All of the `check-*` functions take an expression and the same
;; core keyword arguments.  They use the most common arguments by default
;; and all return a sequence of `simplify-maps` that pass the check-guard.
;;
;; You can pass in your own `:rule` set, check `:guard`, and toggle
;; the `:resolution` to achieve your desired output map sequence.
;;
;; Here are two examples:
;;
;;     (check-expr '(if true :a :b))
;;     (check-expr '(if true :a :b)
;;       :rules      other-rules
;;       :guard      identity
;;       :resolution :subform)

;; `check-aux` is the heart of all the check related functions.
;; The threading expression can be visualized like this `let` block
;; (formatted for space)
;;
;;     (let [simplified-expr
;;             ((res->simplify resolution) expr rules)
;;           simplify-map
;;             (build-simplify-map expr simplified-expr)]
;;       (guard simplify-map))
;;
;; `simply-fn` is built from:
;; `#((res->simplify resolution) % rules)`
(defn- check-aux
  "Simplify an expression, build a simplify-map, and guard the returning map"
  [expr simplify-fn guard]
  (->> expr simplify-fn (build-simplify-map expr) guard))

;; The default resolution is overriden via the `merge`
(defn check-expr
  ""
  [expr & kw-opts]
  (let [{:keys [rules guard resolution]}
        (merge default-args
               {:resolution :toplevel}
               (apply hash-map kw-opts))
        simplify-fn #((res->simplify resolution) % rules)]
    (check-aux expr simplify-fn guard)))

(defn check-reader
  ""
  [reader & kw-opts]
  (let [{:keys [rules guard resolution]}
        (merge default-args
               (apply hash-map kw-opts))
        simplify-fn #((res->simplify resolution) % rules)]
    (keep #(check-aux % simplify-fn guard)
          ((res->read-seq resolution) reader))))

(defn check-file
  ""
  [source-file & kw-opts]
  (let [{:keys [rules guard resolution reporter]
         :or {reporter reporters/cli-reporter}}
        (merge default-args
               (apply hash-map kw-opts))]
    (with-open [reader (io/reader source-file)]
        (doseq [simplify-map (check-reader reader
                                           :rules rules
                                           :guard guard
                                           :resolution resolution)]
          (reporter simplify-map)))))

