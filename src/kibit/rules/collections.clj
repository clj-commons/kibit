(ns kibit.rules.collections
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;;vector
  [(conj [] . ?x) (vector . ?x)]
  [(into [] ?coll) (vec ?coll)]

  ;; empty?
  [(not (empty? ?x)) (seq ?x)]

  ;; set
  [(into #{} ?coll) (set ?coll)])

