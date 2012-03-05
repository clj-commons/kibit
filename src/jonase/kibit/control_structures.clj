(ns jonase.kibit.control-structures)

(def rules
  '{(if ?x ?y nil) when
    (if ?x nil ?y) when-not
    (if ?x (do . ?y)) when
    (if (not ?x) ?y ?z) if-not
    (when (not ?x) . ?y) when-not
    (if true ?x . ?y) "do or removing the if"})

