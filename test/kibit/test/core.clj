(ns kibit.test.core
  (:require [kibit.check :as kibit]
            [kibit.core :as core]
            [clojure.core.logic.unifier :as unifier]
            [kibit.rules :as core-rules]
            [clojure.test :refer [deftest are]]))

(def all-rules (map unifier/prep core-rules/all-rules))

(deftest simplify-alts
  (are [expected-alt test-expr]
       (= expected-alt (core/simplify test-expr all-rules) (:alt (kibit/check-expr test-expr)))
    [1 2 3]           '(do [1 2 3])
    []                '(do [])
    "Hello"           '(do "Hello")
    '(when test then) '(do (when test then))
    :one              '(do :one)
    {:one 1}          '(do {:one 1})
    #{:a :b}          '#{(do :a) (do :b)}))

;; This test confirms when checking will happen and when it won't
(deftest simplify-exprs
  (are [expected-expr test-expr]
       (= expected-expr (:expr (kibit/check-expr test-expr)))
    '(do [1 2 3])   '(do [1 2 3])
    nil             '(if (> 2 3) :one :two)))
