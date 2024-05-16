(ns resources.keyword-suggestions
  (:require [clojure.java.io :as io]))

(defn aliased-keyword-access
  [x]
  (into [] [::io/some-fake-key ::local-key :some/other-key]))

(ns resources.non-conforming)

(require '[clojure.string :as str])

(defn using-a-different-keyword-alias-in-different-ns
  [z]
  (into [] [::str/another-fake-key ::local-key2 :some/other-key2]))

(in-ns 'resources.keyword-suggestions)

(defn flipped-back-aliases-still-there
  [y]
  (into [] [::io/last-fake-key ::local-key3 :some/other-key3]))

(alias 'pprint 'clojure.pprint)

(defn raw-aliases-work
  [y]
  (into [] [::pprint/printing-key ::local-key4 :some/other-key4]))
