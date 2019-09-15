(ns kibit.test.check-reader
  (:require [kibit.check.reader :as reader])
  (:use [clojure.test]))

(deftest derive-aliases-test
  (are [expected-alias-map ns-form]
      (= expected-alias-map (reader/derive-aliases ns-form))
      '{foo foo.bar.baz} '(ns derive.test.one
                            "This is a namespace string, which should not cause problems"
                            {:author "Alice"
                             :purpose "Make sure that attr-map also doesn't cause problems"}
                            (:require [foo.bar.baz :as foo]))
      '{foo foo.bar.baz} '(ns ^{:doc "Docstring as metadata"}
                              derive.test.one
                            (:require [foo.bar.baz :as foo]))
      '{foo foo.bar.baz} '(ns ^:metadata-x derive.test.one
                            (:require [foo.bar.baz :as foo]))

      '{foo foo.bar.baz} '(ns derive.test.one
                            (:require [foo.bar.baz :as foo]))
      '{foo foo.bar.baz
        foom foo.bar.baz.macros} '(ns derive.test.one
                                    (:require [foo.bar.baz :as foo])
                                    (:require-macros [foo.bar.baz.macros :as foom]))
      '{str clojure.string} '(require (quote [clojure.string :as str]))
      '{pprint clojure.pprint} '(alias 'pprint 'clojure.pprint)
      '{string clojure.string} '(require (quote [clojure [string :as string]]))
      '{kibit-check     kibit.check
        kibit-replace   kibit.replace
        kibit-reporters kibit.reporters
        kibit-rules     kibit.rules
        foo-bar-war     foo.bar.war
        foo-baz-waz     foo.baz.waz} '(ns derive.test.one
                                        (:require [kibit
                                                   [check :as kibit-check]
                                                   [replace :as kibit-replace]
                                                   [reporters :as kibit-reporters]
                                                   [rules :as kibit-rules]]
                                                  [foo
                                                   [bar
                                                    [war :as foo-bar-war]]
                                                   [baz
                                                    [waz :as foo-baz-waz]]]))
      '{kibit-check     kibit.check
        kibit-replace   kibit.replace
        kibit-reporters kibit.reporters
        kibit-rules     kibit.rules
        foo-bar-war     foo.bar.war
        foo-baz-waz     foo.baz.waz} '(require (quote [kibit
                                                       [check :as kibit-check]
                                                       [replace :as kibit-replace]
                                                       [reporters :as kibit-reporters]
                                                       [rules :as kibit-rules]])
                                               [foo
                                                [bar
                                                 [war :as foo-bar-war]]
                                                [baz
                                                 [waz :as foo-baz-waz]]])))
