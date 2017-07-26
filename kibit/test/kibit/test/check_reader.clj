(ns kibit.test.check-reader
  (:require [kibit.check.reader :as reader])
  (:use [clojure.test]))

(deftest derive-aliases-test
  (are [expected-alias-map ns-form]
      (= expected-alias-map (reader/derive-aliases ns-form))
      '{foo foo.bar.baz} '(ns derive.test.one
                            (:require [foo.bar.baz :as foo]))
      '{foo foo.bar.baz
        foom foo.bar.baz.macros} '(ns derive.test.one
                                    (:require [foo.bar.baz :as foo])
                                    (:require-macros [foo.bar.baz.macros :as foom]))
      '{str clojure.string} '(require (quote [clojure.string :as str]))))
