(ns jonase.kibit.rules.misc)

(def rules
  '{(apply str (interpose ?x ?y)) clojure.string/join

    ;; mapcat
    (apply concat (apply map ?x ?y)) mapcat
    (apply concat (map ?x . ?y)) mapcat})
