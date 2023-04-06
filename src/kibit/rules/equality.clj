(ns kibit.rules.equality
  (:require [kibit.rules.util :refer [defrules]]))

(defrules rules
  ;; not=
  [(not (= . ?args)) (not= . ?args)]

  ;; zero?
  [(= 0 ?x)  (zero? ?x)]
  [(= ?x 0)  (zero? ?x)]
  [(== 0 ?x) (zero? ?x)]
  [(== ?x 0) (zero? ?x)]

  [(< 0 ?x)  (pos? ?x)]
  [(> ?x 0)  (pos? ?x)]

  [(< ?x 0) (neg? ?x)]
  [(> 0 ?x) (neg? ?x)]

  ;; true? false?
  [(= true ?x) (true? ?x)]
  [(= false ?x) (false? ?x)]

  ; nil?
  [(= ?x nil) (nil? ?x)]
  [(= nil ?x) (nil? ?x)]
  [(not (nil? ?x)) (some? ?x)])

