(ns kibit.test.core
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest simplify-alts
  (are [expected-alt test-form]
       (= expected-alt (:alt (kibit/check-expr test-form)))
    [1 2 3]           '(do [1 2 3])
    []                '(do [])
    "Hello"           '(do "Hello")
    '(when test then) '(do (when test then))
    :one              '(do :one)
    {:one 1}          '(do {:one 1})))

(deftest simplify-exprs
  (are [expected-expr test-expr]
       (= expected-expr (:expr (kibit/check-expr test-expr)))
    '(do [1 2 3])   '(do [1 2 3])
    nil             '(if (> 2 3) :one :two)))

(deftest simplify-deep
  (is (= :one
         (:alt (kibit/check-expr '(if (= 0 0) :one nil))))))

(deftest simplify-one
  (is (= '(when (= 0 0) :one)
         (:alt (kibit/check-expr '(if (= 0 0) :one nil) :resolution :subform)))))

