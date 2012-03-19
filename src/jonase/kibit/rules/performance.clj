(ns jonase.kibit.rules.performance
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  [(reduce + ?coll) (apply + ?coll)]) 