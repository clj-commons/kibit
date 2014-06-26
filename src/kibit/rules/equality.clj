(ns kibit.rules.equality
  (:use [kibit.rules.util :only [defrules]]))

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

  ; nil?
  [(= ?x nil) (nil? ?x)]
  [(= nil ?x) (nil? ?x)])

