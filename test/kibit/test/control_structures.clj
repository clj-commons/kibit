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

(deftest sloppy-if  
  (let [exp-data '(if true (println "X"))
        expected "[Kibit] Consider do or removing the if instead of (if true (println \"X\"))"
        actual (doall (kibit/check-form exp-data))]
    (is (= (count actual) 1))
    (is (= expected (first actual)))))

