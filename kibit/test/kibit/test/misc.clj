(ns kibit.test.misc
  (:require [kibit.check :as kibit]
            [kibit.rules.misc :as misc])
  (:use [clojure.test]))

(deftest class-symbol-are
  (are [valid? class-symbol]
    (= valid? (misc/class-symbol? class-symbol))
    true 'Boolean
    true 'foo.bar.Baz
    false 'boolean
    false 'foo.bar.baz
    false 'foo.bar))

(deftest misc-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(clojure.string/join x y) '(apply str (interpose x y))
    '(clojure.string/join x) '(apply str x)
    '(clojure.string/reverse x) '(apply str (reverse x))
    '(mapcat x y) '(apply concat (apply map x y))
    '(mapcat x y) '(apply concat (map x y))
    '(remove pred coll) '(filter (complement pred) coll)
    '(remove empty? coll) '(filter seq coll)
    '(remove pred coll) '(filter #(not (pred %)) coll)
    '(remove pred coll) '(filter (fn [x] (not (pred x))) coll)
    '(filterv pred coll) '(vec (filter pred coll))
    '(ffirst coll) '(first (first coll))
    '(fnext coll) '(first (next coll))
    '(nnext coll) '(next (next coll))
    '(nfirst coll) '(next (first coll))
    'fun '(fn [args] (fun args))
    'fun '(fn* [args] (fun args))
    '(str x) '(.toString x)
    '(.member obj) '(. obj member)
    '(.method obj args) '(. obj method args)
    '(.method obj args) '(. obj (method args))
    '(Klass/staticMethod args) '(. Klass staticMethod args)
    '(form arg) '(-> arg form)
    '(:form arg) '(-> arg :form)
    '(first-of-form arg rest-of-form) '(-> arg (first-of-form rest-of-form))
    '(form arg) '(->> arg form)
    '(:form arg) '(->> arg :form)
    '(first-of-form rest-of-form arg) '(->> arg (first-of-form rest-of-form))
    '(not-any? pred coll) '(not (some pred coll))
    '(vary-meta arg f) '(with-meta arg (f (meta arg)))
    '(vary-meta arg f :a :b :c) '(with-meta arg (f (meta arg) :a :b :c))))
