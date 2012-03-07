(ns jonase.kibit.control-structures)

(def rules
  '{(if ?x ?y nil) (when ?x ?y)
    (if ?x nil ?y) when-not
    (if ?x (do . ?y)) when
    (if (not ?x) ?y ?z) if-not
    (when (not ?x) . ?y) when-not
    #_(if true ?x . ?y) #_"do or removing the if"})

