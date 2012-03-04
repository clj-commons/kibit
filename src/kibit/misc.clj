(ns kibit.misc)

(def rules
  '{(apply str (interpose ?x ?y)) clojure.string/join
    (apply concat (apply map ?x ?y)) mapcat})