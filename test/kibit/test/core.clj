(ns kibit.test.core
  (:require [jonase.kibit.core :as kibit])
  (:use [clojure.test]))

(deftest simplify-alts
  (are [expected-alt test-form]
       (= expected-alt (:alt (kibit/simplify test-form)))
    [1 2 3]           '(do [1 2 3])
    []                '(do [])
    "Hello"           '(do "Hello")
    '(when test then) '(do (when test then))
    :one              '(do :one)
    {:one 1}          '(do {:one 1})))

(deftest simplify-exprs
  (are [expected-expr test-expr]
       (= expected-expr (:expr (kibit/simplify test-expr)))
    '(do [1 2 3])   '(do [1 2 3])
    nil             '(if (> 2 3) :one :two)))

