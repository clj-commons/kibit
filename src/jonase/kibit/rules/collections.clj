(ns jonase.kibit.rules.collections)

(def rules
  '{;;vector
    (conj [] . ?x) (vector . ?x)})

