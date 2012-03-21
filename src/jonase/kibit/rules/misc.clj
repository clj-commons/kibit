(ns jonase.kibit.rules.misc
  (:require [jonase.kibit.guards :as guards])
  (:use [jonase.kibit.rules.util :only [defrules]]))

(defrules rules
  ;; clojure.string
  [(apply str (interpose ?x ?y)) (clojure.string/join ?x ?y)]
  [(apply str (reverse ?x)) (clojure.string/reverse ?x)]

  ;; mapcat
  [(apply concat (apply map ?x ?y)) (mapcat ?x ?y)]
  [(apply concat (map ?x . ?y)) (mapcat ?x . ?y)]

  ;; filter
  [(filter (complement ?pred) ?coll) (remove ?pred ?coll)]
  [(filter #(not (?pred ?x)) ?coll) (remove ?pred ?coll)]

  ;; Unneeded anonymous functions -- see bug #16
  [(fn ?args (?fun . ?args)) ?fun :when [guards/fn-call?]]
  [(fn* ?args (?fun . ?args)) ?fun :when [guards/fn-call?]]

  ;; do
  [(do ?x) ?x]

  ;; Java stuff
  [(.toString ?x) (str ?x)]

  ;; Threading
  [(-> ?x ?y) (?y ?x)]
  [(->> ?x ?y) (?y ?x)]

  ;; Other
  [(not (= . ?args)) (not= . ?args)])


(comment
  (apply concat (apply map f (apply str (interpose \, "Hello"))))
  (filter (complement nil?) [1 2 3])

  (.toString (apply str (reverse "Hello")))

  (map (fn [x] (inc x)) [1 2 3])
  (map (fn [x] (.method x)) [1 2 3])
  (map #(dec %) [1 2 3])
  (map #(.method %) [1 2 3])

  )
