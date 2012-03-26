(ns kibit.rule-guards
  (:require [clojure.core.logic :as logic]))

(defn not-method? [sym]
  (not= (first (str sym)) \.))

(logic/defne fn-call? [expr]
  ([[_ [_ . _] [fun . _]]]
     (logic/project [fun]
       (logic/pred fun symbol?)
       (logic/pred fun not-method?))))
 
