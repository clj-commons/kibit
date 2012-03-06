(ns kibit.test.kibit-helper
  (:require [jonase.kibit.core :as kibit])
  (:use [clojure.test :only (is)]))

(defn check-form-test [form expected-alt-list line]
  (let  [expected (doall (map #(str "[Kibit] Consider " % " instead of " form " at line " line) expected-alt-list))
         actual  (doall (map :message (kibit/check-form form)))]
    (is  (= expected actual))))

