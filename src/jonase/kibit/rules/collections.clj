(ns jonase.kibit.rules.collections)

(def rules
  '{;;vector
    (conj [] . ?x) (vector . ?x)
    (into [] ?coll) (vec ?coll)

    ;; set
    (into #{} ?coll) (set ?coll)})

