(ns kibit.test.control-structures
  (:require [jonase.kibit.core :as kibit]
            [kibit.test.kibit-helper :as helper])
  (:use [clojure.test]))

;; ==========
;; NOTE
;; ==============
;; YOU SHOULD ALWAYS CHECK WITH ALL RULES
;;
;; Please ensure that new rules generate fully expected results across all
;; rule sets.

(deftest control-structures
  (helper/check-form-test '(if true (println "X")) ['(println "X")])
  (helper/check-form-test '(if true (println "X") nil) ['(when true (println "X"))
                                                        '(println "X")])
  (helper/check-form-test '(if test nil else) ['(when-not test else)])
  (helper/check-form-test '(if test then nil) ['(when test then)]))

