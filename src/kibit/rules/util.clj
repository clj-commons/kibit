(ns kibit.rules.util
  (require [clojure.core.logic :as logic]))

(defn compile-rule [rule]
  (let [[pat alt] (logic/prep rule)]
     [(fn [expr] (logic/== expr pat))
      (fn [sbst] (logic/== sbst alt))]))

(defn raw-rule? [rule]
  (not (vector? rule)))

(defmacro defrules [name & rules]
  (let [rules (for [rule rules]
                (if (raw-rule? rule)
                  rule ;; raw rule, no need to compile
                  (compile-rule rule)))]
    (list 'def name (vec rules))))
