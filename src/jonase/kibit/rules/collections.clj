(ns jonase.kibit.rules.collections
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  ;;vector
  [(conj [] . ?x) (vector . ?x)]
  [(into [] ?coll) (vec ?coll)]

  ;; set
  [(into #{} ?coll) (set ?coll)])

