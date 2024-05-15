(ns kibit.rules.control-structures
  (:require [kibit.rules.util :refer [defrules]]))

(defrules rules
  [(if ?x ?y nil) (when ?x ?y)]
  [(if ?x nil ?y) (when-not ?x ?y)]
  [(if ?x (do . ?y)) (when ?x . ?y)]
  [(if (not ?x) ?y ?z) (if-not ?x ?y ?z)]
  [(if ?x ?x ?y) (or ?x ?y)]
  [(when (not ?x) . ?y) (when-not ?x . ?y)]
  [(do ?x) ?x]
  [(if-let ?binding ?expr nil) (when-let ?binding ?expr)]
  [(when ?x (do . ?y)) (when ?x . ?y)]
  [(when-not ?x (do . ?y)) (when-not ?x . ?y)]
  [(if-not ?x (do . ?y)) (when-not ?x . ?y)]
  [(if-not (not ?x) ?y ?z) (if ?x ?y ?z)]
  [(when-not (not ?x) . ?y) (when ?x . ?y)]

  ;; suggest `while` for bindingless loop-recur
  [(loop [] (when ?test . ?exprs (recur)))
   (while ?test . ?exprs)]
  [(let ?binding (do . ?exprs)) (let ?binding . ?exprs)]
  [(loop ?binding (do . ?exprs)) (loop ?binding . ?exprs)])
