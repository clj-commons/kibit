(ns resources.sets)

(defn killit [coll]
  (not-any? #{"string1" "string2"} (map ffirst coll)))
