(ns kibit.test.reporters
  (:require [kibit.reporters :as reporters]
            [clojure.string :as string]
            [clojure.test :refer :all]))

(deftest plain
  (are [check-map result]
       (= (with-out-str (reporters/cli-reporter check-map))
          (string/join "\n" result))
       {:file "some/file.clj"
        :line 30
        :expr '(+ x 1)
        :alt '(inc x)} ["At some/file.clj:30:"
                        "Consider using:"
                        "  (inc x)"
                        "instead of:"
                        "  (+ x 1)"
                        "" ""]))
(deftest gfm
  (are [check-map result]
       (= (with-out-str (reporters/gfm-reporter check-map))
          (string/join "\n" result))
       {:file "some/file.clj"
        :line 30
        :expr '(+ x 1)
        :alt '(inc x)} ["----"
                        "##### `some/file.clj:30`"
                        "Consider using:"
                        "```clojure"
                        "  (inc x)"
                        "```"
                        "instead of:"
                        "```clojure"
                        "  (+ x 1)"
                        "```"
                        "" ""]))
