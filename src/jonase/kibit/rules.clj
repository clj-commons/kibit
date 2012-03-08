(ns jonase.kibit.rules
  "`rules.clj` provides the core functionality for extracting
  and merging rules from namespaces.  There are shorthand `def`s
  for rule the core rule sets"
  (:require [jonase.kibit.rules.arithmetic :as arith]
            [jonase.kibit.rules.control-structures :as control]
            [jonase.kibit.rules.misc :as misc]))

(def rule-map {:control-structures control/rules
               :arithmetic arith/rules
               :misc misc/rules})

;; TODO: Consider a refactor for this into a function
;; `(defn rules-for-ns [& namespaces])`
(def all-rules (apply merge (vals rule-map)))

