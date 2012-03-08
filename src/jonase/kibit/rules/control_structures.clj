(ns jonase.kibit.rules.control-structures)

(def rules
  '{(if ?x ?y nil) (when ?x ?y)
    (if ?x nil ?y) (when-not ?x ?y)
    (if ?x (do . ?y)) (when ?x ?y)
    (if (not ?x) ?y ?z) (if-not ?x ?y ?z)
    (when (not ?x) . ?y) (when-not ?x ?y)
    (if true ?x ?y) ?x
    (when true . ?x) (do . ?x)
    (do ?x) ?x
    (when-not true ?x) "removing this dead code"})

(comment
  (when true
    (if (pred? x)
      (do
        (action a)
        (action b)))))
