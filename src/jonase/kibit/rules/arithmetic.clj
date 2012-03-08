(ns jonase.kibit.rules.arithmetic)

(def rules
  '{(+ ?x 1) (inc ?x)
    (+ 1 ?x) (inc ?x)
    (- ?x 1) (dec ?x)

    (= 0 ?x)  (zero? ?x)
    (= ?x 0)  (zero? ?x)
    (== 0 ?x) (zero? ?x)
    (== ?x 0) (zero? ?x)
    
    (< 0 ?x)  (pos? ?x)
    (> ?x 0)  (pos? ?x)
    (<= 1 ?x) (pos? ?x)

    (< ?x 0) (neg? ?x)
    
    (= ?x ?x)  true
    (== ?x ?x) true
    (zero? 0)  true})

