(ns kibit.rules.equality
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;; not=
  {:rule [(not (= . ?args)) (not= . ?args)]}

  ;; zero?
  {:rule [(= 0 ?x)  (zero? ?x)]}
  {:rule [(= ?x 0)  (zero? ?x)]}
  {:rule [(== 0 ?x) (zero? ?x)]}
  {:rule [(== ?x 0) (zero? ?x)]}

  {:rule [(< 0 ?x)  (pos? ?x)]}
  {:rule [(> ?x 0)  (pos? ?x)]}

  {:rule [(< ?x 0) (neg? ?x)]}
  {:rule [(> 0 ?x) (neg? ?x)]}

  ;; true? false?
  {:rule [(= true ?x) (true? ?x)]}
  {:rule [(= false ?x) (false? ?x)]}

  ; nil?
  {:rule [(= ?x nil) (nil? ?x)]}
  {:rule [(= nil ?x) (nil? ?x)]})

