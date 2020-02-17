(ns kibit.rules.collections
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  ;;vector
  {:rule [(conj [] . ?x) (vector . ?x)]}
  {:rule [(into [] ?coll) (vec ?coll)]}
  {:rule [(assoc ?coll ?key0 (assoc (?key0 ?coll) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]}
  {:rule [(assoc ?coll ?key0 (assoc (?coll ?key0) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]}
  {:rule [(assoc ?coll ?key0 (assoc (get ?coll ?key0) ?key1 ?val)) (assoc-in ?coll [?key0 ?key1] ?val)]}
  {:rule [(assoc ?coll ?key (?fn (?key ?coll) . ?args)) (update-in ?coll [?key] ?fn . ?args)]}
  {:rule [(assoc ?coll ?key (?fn (?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]}
  {:rule [(assoc ?coll ?key (?fn (get ?coll ?key) . ?args)) (update-in ?coll [?key] ?fn . ?args)]}
  {:rule [(update-in ?coll ?keys assoc ?val) (assoc-in ?coll ?keys ?val)]}

  ;; empty?
  {:rule [(not (empty? ?x)) (seq ?x)]}
  {:rule [(when-not (empty? ?x) . ?y) (when (seq ?x) . ?y)]}

  ;; set
  {:rule [(into #{} ?coll) (set ?coll)]}

  {:rule [(dorun (map ?fn ?coll)) (run! ?fn ?coll)]}
  {:rule [(take ?n (repeatedly ?coll)) (repeatedly ?n ?coll)]})
