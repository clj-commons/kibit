(ns kibit.test.collections
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest collections-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(vector a) '(conj [] a)
    '(vector a b) '(conj [] a b)
    '(vec coll) '(into [] coll)
    '(set coll) '(into #{} coll)
    '(update-in coll [k] f) '(assoc coll k (f (k coll)))
    '(update-in coll [k] f) '(assoc coll k (f (coll k)))
    '(update-in coll [k] f) '(assoc coll k (f (get coll k)))
    '(assoc-in coll [k0 k1] a) '(assoc coll k0 (assoc (k0 coll) k1 a))
    '(assoc-in coll [k0 k1] a) '(assoc coll k0 (assoc (coll k0) k1 a))
    '(assoc-in coll [k0 k1] a) '(assoc coll k0 (assoc (get coll k0) k1 a))
    '(update-in coll [k] f a b c) '(assoc coll k (f (k coll) a b c))
    '(update-in coll [k] f a b c) '(assoc coll k (f (coll k) a b c))
    '(update-in coll [k] f a b c) '(assoc coll k (f (get coll k) a b c))
    '(assoc-in coll [k1 k2] v) '(update-in coll [k1 k2] assoc v)
    '(repeatedly 10 (constantly :foo)) '(take 10 (repeatedly (constantly :foo)))

    ;; some wrong simplifications happened in the past:
    nil '(assoc coll k (assoc (coll k0) k1 a))
    nil '(assoc coll k (assoc (get coll k0) k1 a))
    nil '(assoc coll k (assoc (k0 coll) k1 a))))
