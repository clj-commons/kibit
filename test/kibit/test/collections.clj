(ns kibit.test.collections
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest collections-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(seq a) '(not (empty? a))
    '(when (seq a) b) '(when-not (empty? a) b)
    '(when (seq a) b) '(when (not (empty? a)) b)
    '(vector a) '(conj [] a)
    '(vector a b) '(conj [] a b)
    '(vec coll) '(into [] coll)
    '(set coll) '(into #{} coll)
    '(update-in coll [k] f) '(assoc coll k (f (k coll)))
    '(update-in coll [k] f) '(assoc coll k (f (coll k)))
    '(update-in coll [k] f) '(assoc coll k (f (get coll k)))
    '(update-in coll [k] f a b c) '(assoc coll k (f (k coll) a b c))
    '(update-in coll [k] f a b c) '(assoc coll k (f (coll k) a b c))
    '(update-in coll [k] f a b c) '(assoc coll k (f (get coll k) a b c))))
