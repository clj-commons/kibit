(ns kibit.rules.util
  (:require [clojure.core.logic :as logic]
            [clojure.core.logic.unifier :as unifier]
            [clojure.walk :as walk]))

;; wrap vectors in an s-expression of form (:kibit.rules/vector ...)
(def wrap-vector-walker 
  (partial walk/prewalk
           #(if (vector? %)
              (concat `(:kibit.rules/vector) %)
              %)))

(defn kibit-vector? [exp]
  (and (sequential? exp) (= :kibit.rules/vector (first exp))))

;; If we catch an exp in the form (:kibit.rules/vector foo bar & baz) we return 
;; the converted back into a normal vector 
(def unwrap-vector-walker
  (partial walk/prewalk
           (fn [exp]
             (if (kibit-vector? exp)
               (apply vector (rest exp))
               exp))))

(defn compile-rule [[pattern simplification]]
  (let [rule [pattern (wrap-vector-walker simplification)]
        [pat alt] (unifier/prep rule)]
    [(fn [expr] (logic/== expr pat))
      (fn [sbst] (logic/== sbst alt))]))
            
(defn raw-rule? [rule]
  (not (vector? rule)))

(defmacro defrules [name & rules]
  `(let [rules# (for [rule# '~rules]
                  (if (raw-rule? rule#)
                    (eval rule#) ;; raw rule, no need to compile
                    (compile-rule rule#)))]
     (def ~name (vec rules#))))
