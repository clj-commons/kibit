(ns kibit.check.reader
  (:require [clojure.tools.reader :as reader])
  (:import [clojure.lang LineNumberingPushbackReader]))

;; Preprocessing
;; -------------
;; Alias pre-processing

;; It is necessary at read time to maintain a small amount of state
;; about the contents of the stream read so far to enable the proper
;; reading of aliased keywords. Clojure accomplishes this during
;; `(:require ...)` because evaluation and compilation is interlaced
;; with reading so as to establish aliases in a namespace as it is
;; being loaded. To do this statically we need to maintain a temporary
;; table of namespaces to aliases. Additionally, we cannot simply use
;; a map of aliases as it is possible to switch namespaces mid-file
;; and get one stream to effectively hop between two namespaces.

(defmulti derive-aliases first :default 'ns)

(defn unquote-if-quoted
  [form]
  (if (and (seq? form)
           (= 'quote (first form)))
    (second form)
    form))

;; NOTE: `prefix-spec?`, `options-spec?`, and `deps-from-libspec` were derived
;; from the private fns defined in [clojure.tools.namespace.parse][1]
;; [1]: https://github.com/clojure/tools.namespace
(defn- prefix-spec?
  "Returns true if form represents a libspec prefix list like
  (prefix name1 name1) or [com.example.prefix [name1 :as name1]]"
  [form]
  (and (sequential? form)  ; should be a list, but often is not
       (symbol? (first form))
       (not-any? keyword? form)
       (< 1 (count form))))  ; not a bare vector like [foo]

(defn- option-spec?
  "Returns true if form represents a libspec vector containing optional
  keyword arguments like [namespace :as alias] or
  [namespace :refer (x y)] or just [namespace]"
  [form]
  (and (sequential? form)  ; should be a vector, but often is not
       (symbol? (first form))
       (or (keyword? (second form))  ; vector like [foo :as f]
           (= 1 (count form)))))  ; bare vector like [foo]

(defn- deps-from-libspec
  "A slight modification from clojure.tools.namespace.parse/deps-from-libspec,
  in which aliases are captured as metadata."
  [prefix form alias]
  (cond (prefix-spec? form)
        (mapcat (fn [f] (deps-from-libspec
                         (symbol (str (when prefix (str prefix "."))
                                      (first form)))
                         f
                         nil))
                (rest form))

        (option-spec? form)
        (let [[_ as? form-alias] form]
          (deps-from-libspec prefix (first form) (when (= :as as?) form-alias)))

        (symbol? form)
        (list (with-meta
                (symbol (str (when prefix (str prefix ".")) form))
                {:alias alias}))

        (keyword? form) ; Some people write (:require ... :reload-all)
        nil
        :else
        (throw (ex-info "Unparsable namespace form"
                        {:reason ::unparsable-ns-form
                         :form   form}))))

(defn derive-aliases-from-deps
  "Takes a vector of `deps`, of which each element is in the form accepted by
  the `ns` and `require` functions to specify dependencies. Returns a map where
  each key is a clojure.lang.Symbol that represents the alias, and each value
  is the clojure.lang.Symbol that represents the namespace that the alias refers to."
  [deps]
  (->> deps
       (mapcat #(deps-from-libspec nil (unquote-if-quoted %) nil))
       (remove (comp nil? :alias meta))
       (into {} (map (fn [dep] [(-> dep meta :alias) dep])))))

(defmethod derive-aliases 'ns
  [[_ _ns & ns-asserts]]
  (->> ns-asserts
       (group-by first)
       ((juxt :require :require-macros))
       (apply concat)
       (map (comp derive-aliases-from-deps rest))
       (apply merge)))

(defmethod derive-aliases 'require
  [[_ & deps]]
  (derive-aliases-from-deps deps))

(defmethod derive-aliases 'alias
  [[_ alias namespace-sym]]
  ;; Remove quotes
  {(second alias) (second namespace-sym)})

;; Reading source files
;; --------------------
;; ### Extracting forms

;; `read-file` is intended to be used with a Clojure source file,
;; read in by Clojure's LineNumberingPushbackReader *(LNPR)*. Expressions are
;; extracted using the clojure reader (ala `read`), and line numbers
;; are added as `:line` metadata to the forms (via LNPR).

(defn- careful-refer
  "Refers into the provided namespace all public vars from clojure.core
except for those that would clobber any existing interned vars in that
namespace.  This is needed to ensure that symbols read within syntax-quote
end up being fully-qualified to clojure.core as appropriate, and only
to *ns* if they're not available there.  AFAICT, this will work for all
symbols in syntax-quote except for those referring to vars that are referred
into the namespace."
  [ns]
  (binding [*ns* ns]
    (refer 'clojure.core :exclude (or (keys (ns-interns ns)) ())))
  ns)

(def eof (Object.))

(defn read-file
  "Generate a lazy sequence of top level forms from a
   LineNumberingPushbackReader"
  [^LineNumberingPushbackReader r init-ns]
  (let [ns (careful-refer (create-ns init-ns))
        do-read (fn do-read [ns alias-map]
                  (lazy-seq
                   (let [form (binding [*ns* ns
                                        reader/*alias-map* (merge (ns-aliases ns)
                                                                  (alias-map ns))]
                                (reader/read r false eof))
                         [ns? new-ns k] (when (sequential? form) form)
                         new-ns (unquote-if-quoted new-ns)
                         ns (if (and (symbol? new-ns)
                                     (#{'ns 'in-ns} ns?))
                              (careful-refer (create-ns new-ns))
                              ns)
                         alias-map (if (#{'require 'ns 'alias} ns?)
                                     (update alias-map ns
                                             merge
                                             (derive-aliases form))
                                     alias-map)]
                     (when-not (= form eof)
                       (cons form (do-read ns alias-map))))))]
    (do-read ns {ns {}})))
