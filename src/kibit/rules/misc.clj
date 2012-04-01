(ns kibit.rules.misc
  (:require [clojure.core.logic :as logic])
  (:use [kibit.rules.util :only [defrules]]))

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

  ;; Unneeded anonymous functions
  (let [fun (logic/lvar)
        args (logic/lvar)]
    [(fn [expr]
       (logic/all
        (logic/conde
         [(logic/== expr (list 'fn args (logic/llist fun args)))]
         [(logic/== expr (list 'fn* args (logic/llist fun args)))])
        (logic/pred fun #(or (keyword? %)
                             (and (symbol? %)
                                  (not= \. (first (str %))))))))
     #(logic/== % fun)])
  
   ;; do
  [(do ?x) ?x]

  ;; Java stuff
  [(.toString ?x) (str ?x)]
  
  (let [obj (logic/lvar)
        method (logic/lvar)
        args (logic/lvar)]
    [#(logic/== % (logic/llist '. obj method args))
     #(logic/project [method args]
        (logic/== % `(~(symbol (str "." method)) ~obj ~@args)))])

  
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

  (map (fn [m] (:key m)) [some maps])
  (map (fn [m] (:key m alt)) [a b c])

  (. obj toString)
  (. obj toString a b c))
