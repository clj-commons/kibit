(ns kibit.test.equality
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest equality-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(not= x b) '(not (= x b))
    '(zero? x) '(= 0 x)
    '(zero? x) '(= x 0)
    '(zero? x) '(== 0 x)
    '(zero? x) '(== x 0)
    '(pos? x) '(< 0 x)
    '(pos? x) '(> x 0)
    '(neg? x) '(< x 0)
    '(nil? x) '(= nil x)
    '(nil? x) '(= x nil)))

