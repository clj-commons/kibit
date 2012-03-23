(ns jonase.kibit.rules.control-structures
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  [(if ?x ?y nil) (when ?x ?y)]
  [(if ?x nil ?y) (when-not ?x ?y)]
  [(if ?x (do . ?y)) (when ?x ?y)]
  [(if (not ?x) ?y ?z) (if-not ?x ?y ?z)]
  [(when (not ?x) . ?y) (when-not ?x . ?y)]
  [(if true ?x ?y) ?x]
  [(if true ?x) ?x]
  [(when true . ?x) (do . ?x)]
  [(do ?x) ?x]
  [(when-not true ?x) _]
  [(when false ?x) _]
  [(if-let ?binding ?expr nil) (when-let ?binding ?expr)])

(comment
  (when (not (pred? x y)) (f x y))
  
  (if (pred? x)
    (do (action a)
        (action b)
        (if-let [x (f a b c)
                 y (g a b c)
                 z (h a b c)]
          (do (+ 1 0)
              (= 1 1)
              (< 1 0)))
        (action d)
        (action f))
    nil))
      
