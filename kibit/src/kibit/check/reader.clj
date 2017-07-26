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

(defn derive-alias-from-dep
  [[dep & rest]]
  (let [seq? (seq rest)
        index (and seq?
                   (first (keep-indexed #(if (= :as %2) %1)
                                        rest)))
        alias (and index
                   (nth rest (inc index)))]
    (when alias
      [alias dep])))

(defn unquote-if-quoted
  [form]
  (if (and (seq? form)
           (= 'quote (first form)))
    (second form)
    form))

(defmethod derive-aliases 'ns
  [[_ _ns & ns-asserts]]
  (->> ns-asserts
       (group-by first)
       (reduce (fn [m [k v]]
                 (assoc m k (mapcat rest v)))
               {})
       ((juxt :require :require-macros))
       (apply concat)
       (map derive-alias-from-dep)
       (remove nil?)
       (into {})))

(defmethod derive-aliases 'require
  [[_ & deps]]
  (->> deps
       (map (comp derive-alias-from-dep unquote-if-quoted))
       (remove nil?)
       (into {})))

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

(defn trace [v] (println v) v)

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

