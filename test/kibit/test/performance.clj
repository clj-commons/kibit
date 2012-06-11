(ns kibit.test.performance
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest performance-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(apply + coll) '(reduce + coll)))
