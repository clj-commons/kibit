(ns kibit.rules.performance
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;; reduce on var-arg functions
  [(reduce + ?coll) (apply + ?coll)])

