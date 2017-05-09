(ns resources.sets)

(def Z_4
  "This just shows that #78"
  #{#{}
    #{#{}}
    #{#{#{}}}
    #{#{#{#{}}}}
    #{#{#{#{#{}}}}}})

(defn killit [coll]
  (not-any? #{"string1" "string2"} (map ffirst coll)))
