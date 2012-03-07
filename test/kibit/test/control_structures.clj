(ns kibit.test.control-structures
  (:require [jonase.kibit.core :as kibit])
  (:use [clojure.test]))

;; ==========
;; NOTE
;; ==============
;; YOU SHOULD ALWAYS CHECK WITH ALL RULES
;;
;; Please ensure that new rules generate fully expected results across all
;; rule sets.

(deftest control-structures-are
  (are [expected-alt-form test-form] 
       (= expected-alt-form (:alt (kibit/check-form test-form)))
    '(println "X") '(if true (println "X") nil)
    '(println "X") '(if true (println "X"))
    '(when-not test else) '(if test nil else)
    '(when test then) '(if test then nil)))

