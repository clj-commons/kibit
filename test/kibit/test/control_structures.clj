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

(deftest control-structures
  (are [form expected-alt-list] 
       (= expected-alt-list (:alt (kibit/check-form form)))
    '(if true (println "X")) '(println "X")
    '(if true (println "X") nil) '(println "X")
    '(if test nil else) '(when-not test else)
    '(if test then nil) '(when test then)))

