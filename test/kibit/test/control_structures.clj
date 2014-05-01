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
    '(or test else) '(if test test else)
    '(when-not test then) '(when (not test) then)
    'single-expression '(do single-expression)
    '(when-let [a test] expr) '(if-let [a test] expr nil)
    '(let [a 1] (println a) a) '(let [a 1] (do (println a) a))
    '(when test (println a) then) '(when test (do (println a) then))
    '(when-not test (println a) then) '(when-not test (do (println a) then))
    '(when-not test body) '(if (not test) (do body))
    '(when-not test body) '(if-not test (do body))
    
    '(loop [a 4] (println a) (if (zero? a) a (recur (dec a))))
    '(loop [a 4] (do (println a) (if (zero? a) a (recur (dec a)))))))
