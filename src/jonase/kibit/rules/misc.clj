(ns jonase.kibit.rules.misc)

(def rules
  '{;; clojure.string
    (apply str (interpose ?x ?y)) (clojure.string/join ?x ?y)
    (apply str (reverse ?x)) (clojure.string/reverse ?x)
    
    ;; mapcat
    (apply concat (apply map ?x ?y)) (mapcat ?x ?y)
    (apply concat (map ?x . ?y)) (mapcat ?x . ?y)

    ;; filter
    (filter (complement ?pred) ?coll) (remove ?pred ?coll) 
    (filter #(not (?pred ?x)) ?coll) (remove ?pred ?coll)

    ;; Unneeded anonymous functions
    (fn ?args (?fun . ?args)) ?fun
    (fn* ?args (?fun . ?args)) ?fun

    ;; Java stuff
    (.toString ?x) (str ?x)})

(comment
  (apply concat (apply map f (apply str (interpose \, "Hello"))))
  (filter (complement nil?) [1 2 3])

  (.toString (apply str (reverse "Hello")))
  
  (map (fn [x] (inc x)) [1 2 3])
  (map #(dec %) [1 2 3]))
