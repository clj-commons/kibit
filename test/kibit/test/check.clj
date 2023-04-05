(ns kibit.test.check
  (:require [kibit.check :as kibit]
            [clojure.test :refer [deftest are]]))

;; These tests are identical to the tests in kibit.test.core
;; They are here to illustrate kibit use via `check`

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
