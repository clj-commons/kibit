(ns jonase.kibit.rules.performance
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  ;; reduce on var-arg functions
  [(reduce + ?coll) (apply + ?coll)])

