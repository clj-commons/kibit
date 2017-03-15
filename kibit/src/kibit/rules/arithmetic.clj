(ns kibit.rules.arithmetic
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  {:rule [(+ ?x 1) (inc ?x)]}
  {:rule [(+ 1 ?x) (inc ?x)]}
  {:rule [(- ?x 1) (dec ?x)]}

  {:rule [(* ?x (* . ?xs)) (* ?x . ?xs)]}
  {:rule [(+ ?x (+ . ?xs)) (+ ?x . ?xs)]}

  ;;trivial identites
  {:rule [(+ ?x 0) ?x]}
  {:rule [(- ?x 0) ?x]}
  {:rule [(* ?x 1) ?x]}
  {:rule [(/ ?x 1) ?x]}
  {:rule [(* ?x 0) 0]}

  ;;Math/hypot
  {:rule [(Math/sqrt (+ (Math/pow ?x 2) (Math/pow ?y 2))) (Math/hypot ?x ?y)]}

  ;;Math/expm1
  {:rule [(dec (Math/exp ?x)) (Math/expm1 ?x)]}

  ;;ugly rounding tricks
  {:rule [(long (+ ?x 0.5)) (Math/round ?x)]}
)


