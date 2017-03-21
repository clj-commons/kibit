(ns kibit.monkeypatch
  "Various helpers providing a with-monkeypatches form which wraps"
  (:require [clojure.core.logic :as c.c.l])
  (:import [clojure.lang
            Var
            IPersistentSet]
           [clojure.core.logic.protocols
            ITreeTerm]))

(defn ^:pivate tree-term? [x]
  (and (or (coll? x)
           (instance? ITreeTerm x))
       (not (instance? IPersistentSet x))))

(def kibit-redefs
  {#'c.c.l/tree-term? tree-term?})

(defmacro with-monkeypatches
  "Builds a try/finally which captures Var bindings (and ^:macro tags) comming in, creates new Var
  bindings within the try and in the finally restores the original bindings. This allows users to
  establish stack-local patched contexts."
  {:style/indent [1]}
  [redefs & forms]
  (let [redefs            (eval redefs)
        original-bindings (into {}
                                (for [k (keys redefs)]
                                  [k (gensym)]))]
    `(let [~@(for [[k v] original-bindings
                   f     [v `(deref ~k)]]
               f)]
       (try ~@(for [[k v] redefs]
                `(do (.bindRoot ~k ~v)
                     ~(if (.isMacro ^Var k)
                        `(.setMacro ~k))))
            ~@forms
            (finally
              ~@(for [[k v] redefs]
                  `(do (.bindRoot ~k ~(get original-bindings k))
                       ~(if (.isMacro ^Var k)
                          `(.setMacro ~k)))))))))
