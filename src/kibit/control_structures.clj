(ns kibit.control-structures)

(def rules
  '{(if ?x ?y nil) when
    (if ?x nil ?y) when-not
    (if (not ?x) ?y ?z) if-not})
    