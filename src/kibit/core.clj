(ns kibit.core
  "Kibit's core functionality uses core.logic to suggest idiomatic
   replacements for patterns of code."
  (:require [clojure.walk :as walk]
            [clojure.core.logic :as logic]))

;; Building an alternative form
;; ----------------------------
;;
;; ### Applying unification

(logic/defne check-guards [expr guards]
  ([_ ()])
  ([_ [guard-fn . rest]]
     (logic/project [guard-fn]
       (guard-fn expr))
     (check-guards expr rest)))

;; Performs the first simplification found in the rules. If no rules
;; apply the original expression is returned. Does not look at
;; subforms.
(defn simplify-one [expr rules]
  (let [alts (logic/run* [q]
               (logic/fresh [pat guards alt]
                 (logic/membero [pat guards alt] rules)
                 (logic/== pat expr)
                 (check-guards expr guards)
                 (logic/== q alt)))]
    (if (empty? alts) expr (first alts))))

;; Simplifies expr according to the rules until no more rules apply.
(defn simplify [expr rules]
  (->> expr
       (iterate (partial walk/prewalk #(simplify-one % rules)))
       (partition 2 1)
       (drop-while #(apply not= %))
       (ffirst)))

