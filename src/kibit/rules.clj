(ns kibit.rules
  "`rules.clj` provides the core functionality for extracting
  and merging rules from namespaces.  There are shorthands for
  the individual rule sets, via the `rule-map`"
  (:require [kibit.rules.arithmetic :as arith]
            [kibit.rules.control-structures :as control]
            [kibit.rules.collections :as coll]
            [kibit.rules.equality :as equality]
            [kibit.rules.misc :as misc]
            [clojure.core.logic :as logic]))


;; More information on rules
;; -------------------------
;;
;; Rule sets are stored in individual files that have a top level
;; `(defrules rules ...)`.  The collection of rules are in the `rules`
;; directory.
;;
;; TODO Paul - Major revisions
;; Each rule (also called a rule pair) in a rule set map is comprised of:
;;
;; * a pattern expression (e.g. `(+ ?x 1)`)
;; * a substitution expression (e.g. `(inc ?x)`
;;
;; These rules are used in the unifcation process to generate suggested
;; code alternatives.  For more information see:
;; [core](#jonase.kibit.core) namespace


;; A map of the individual rule sets, keyed by rule group
(def rule-map {:control-structures control/rules
               :arithmetic arith/rules
               :collections coll/rules
               :equality equality/rules
               :misc misc/rules})

;; TODO: Consider a refactor for this into a function
;; `(defn rules-for-ns [& namespaces])`
(def all-rules (map logic/prep (apply concat (vals rule-map))))
