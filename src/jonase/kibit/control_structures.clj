(ns jonase.kibit.control-structures)

(def rules
  '{(if ?x ?y nil) (when ?x ?y)
    (if ?x nil ?y) when-not
    (if ?x (do . ?y)) when
    (if (not ?x) ?y ?z) if-not
    (when (not ?x) . ?y) when-not
    (if true ?x . ?y) ?x
    (when true ?x) ?x}) ; Maybe this should be (do ?x)

