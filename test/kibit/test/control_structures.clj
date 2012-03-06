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

(defn test-check-form [form alt]
  (let [expected (str "[Kibit] Consider " alt " instead of " form)
        actual (doall (kibit/check-form form))]
    (is (= (count actual) 1))
    (is (= expected (first actual))))) 

(deftest control-structures
  (test-check-form '(if true (println "X")) '(println "X"))
  (test-check-form '(if test nil else) '(when-not test else))
  (test-check-form '(if test then nil) 'when))
