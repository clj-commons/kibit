(ns jonase.kibit.rules.misc)

(def rules
  '{(apply str (interpose ?x ?y)) (clojure.string/join ?x ?y)

    ;; mapcat
    (apply concat (apply map ?x ?y)) (mapcat ?x ?y)
    (apply concat (map ?x . ?y)) (mapcat ?x . ?y)

    ;; filter
    (filter (complement ?pred) ?coll) (remove ?pred ?coll) 
    (filter #(not (?pred ?x)) ?coll) (remove ?pred ?coll)

    (fn ?args (?fun . ?args)) ?fun
    (fn* ?args (?fun . ?args)) ?fun

    ;;do
    (do ?x) ?x

    })

(comment
  (apply concat (apply map f (apply str (interpose \, "Hello"))))
  (filter (complement nil?) [1 2 3])

  (map (fn [x] (inc x)) [1 2 3])
  (map #(dec %) [1 2 3]))
