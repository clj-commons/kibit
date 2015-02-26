(ns kibit.rules.arithmetic
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  [(+ ?x 1) (inc ?x)]
  [(+ 1 ?x) (inc ?x)]
  [(- ?x 1) (dec ?x)]

  [(* ?x (* . ?xs)) (* ?x . ?xs)]
  [(+ ?x (+ . ?xs)) (+ ?x . ?xs)]

  ;;trivial identites
  [(+ ?x 0) ?x]
  [(- ?x 0) ?x]
  [(* ?x 1) ?x]
  [(/ ?x 1) ?x]
  [(* ?x 0) 0]
  [(* ?x) ?x]
  [(+ ?x) ?x]

  ;;Math/hypot
  [(Math/sqrt (+ (Math/pow ?x 2) (Math/pow ?y 2))) (Math/hypot ?x ?y)]

  ;;Math/expm1
  [(dec (Math/exp ?x)) (Math/expm1 ?x)]

  ;;ugly rounding tricks
  [(long (+ ?x 0.5)) (Math/round ?x)]
)


