(ns jonase.kibit.rules
  "`rules.clj` provides the core functionality for extracting
  and merging rules from namespaces.  There are shorthand `def`s
  for rule the core rule sets"
  (:require [jonase.kibit.arithmetic :as arith]
            [jonase.kibit.control-structures :as control]
            [jonase.kibit.misc :as misc]))

(def rule-map {:control-structures control/rules
               :arithmetic arith/rules
               :misc misc/rules})

;; TODO: Consider a refactor for this into a function
;; `(defn rules-for-ns [& namespaces])`
(def all-rules (merge (values rule-map)))

