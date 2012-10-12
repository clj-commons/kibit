(ns kibit.test.control-structures
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest control-structures-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(when test then) '(if test then nil)
    '(when-not test else) '(if test nil else)
    '(when test body) '(if test (do body))
    '(if-not test then else) '(if (not test) then else)
    '(when-not test then) '(when (not test) then)
    '(println "X") '(if true (println "X") nil)
    '(println "X") '(if true (println "X"))
    '(do body-1 body-2) '(when true body-1 body-2)
    'single-expression '(do single-expression)
    '_ '(when-not true anything)
    '_ '(when false anything)
    '(when-let [a test] expr) '(if-let [a test] expr nil)
    '(if-let [a test] then else) '(let [a test] (if a then else))
    '(when-let [a test] expr) '(let [a test] (if a expr nil))
    '(let [a 1] (println a) a) '(let [a 1] (do (println a) a))))
