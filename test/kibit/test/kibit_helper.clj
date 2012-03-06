(ns kibit.test.kibit-helper
  (:require [jonase.kibit.core :as kibit])
  (:use [clojure.test :only (is)]))

(defn check-form-test [form expected-alt-list]
  (let  [expected expected-alt-list
         actual (doall (map :alt (kibit/check-form form)))]
    (is  (= expected actual))))

