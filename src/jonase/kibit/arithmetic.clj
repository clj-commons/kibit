(ns jonase.kibit.arithmetic)

(def rules
  '{(+ ?x 1) inc
    (+ 1 ?x) inc
    (- ?x 1) dec

    (= 0 ?x)  zero?
    (= ?x 0)  zero?
    (== 0 ?x) zero?
    (== ?x 0) zero?
    
    (< 0 ?x)  pos?
    (> ?x 0)  pos?
    (<= 1 ?x) pos?

    (< ?x 0) neg?})

    
    
   