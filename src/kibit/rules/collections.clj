(ns kibit.rules.collections
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;;vector
  [(conj [] . ?x) (vector . ?x)]
  [(into [] ?coll) (vec ?coll)]
  [(assoc ?coll ?key (?fn (?key ?coll) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(assoc ?coll ?key (?fn (?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(assoc ?coll ?key (?fn (get ?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(update-in ?coll ?keys assoc ?val) (assoc-in ?coll ?keys ?val)]

  ;; empty?
  [(not (empty? ?x)) (seq ?x)]
  [(when-not (empty? ?x) . ?y) (when (seq ?x) . ?y)]

  ;; set
  [(into #{} ?coll) (set ?coll)])

