(ns kibit.test.arithmetic
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest arithmetic-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(inc num) '(+ num 1)
    '(inc num) '(+ 1 num)
    '(dec num) '(- num 1)
    '(* x y z) '(* x (* y z))
    '(+ x y z) '(+ x (+ y z))))
