(ns jonase.kibit.rules.arithmetic
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  [(+ ?x 1) (inc ?x)]
  [(+ 1 ?x) (inc ?x)]
  [(- ?x 1) (dec ?x)]

  [(* ?x (* . ?xs)) (* ?x . ?xs)]
  [(+ ?x (+ . ?xs)) (+ ?x . ?xs)])

