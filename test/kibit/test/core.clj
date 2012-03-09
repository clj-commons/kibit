(ns kibit.test.core
  (:require [jonase.kibit.core :as kibit])
  (:use [clojure.test]))

(deftest check-form-alts
  (are [expected-alt test-form]
       (= expected-alt (:alt (kibit/check-form test-form)))
    [1 2 3]           '(do [1 2 3])
    []                '(do [])
    "Hello"           '(do "Hello")
    '(when test then) '(do (when test then))
    :one              '(do :one)
    {:one 1}          '(do {:one 1})))

