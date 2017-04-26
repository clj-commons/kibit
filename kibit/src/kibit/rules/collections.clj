(ns kibit.rules.collections
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;;vector
  [(conj [] . ?x) (vector . ?x)]
  [(into [] ?coll) (vec ?coll)]
  [(assoc ?coll ?key0 (assoc (?key0 ?coll) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]
  [(assoc ?coll ?key0 (assoc (?coll ?key0) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]
  [(assoc ?coll ?key0 (assoc (get ?coll ?key0) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]
  [(assoc ?coll ?key (?fn (?key ?coll) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(assoc ?coll ?key (?fn (?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(assoc ?coll ?key (?fn (get ?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]
  [(update-in ?coll ?keys assoc ?val) (assoc-in ?coll ?keys ?val)]

  ;; empty?
  [(not (empty? ?x)) (seq ?x)]
  [(when-not (empty? ?x) . ?y) (when (seq ?x) . ?y)]

  ;; set
  [(into #{} ?coll) (set ?coll)]

  [(take ?n (repeatedly ?coll)) (repeatedly ?n ?coll)]
  [(doall (map ?fn ?coll)) (run! ?fn ?coll)]
  [(doall (mapv ?fn ?coll)) (run! ?fn ?coll)])

