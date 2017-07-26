(ns resources.keywords
  (:require [clojure.java.io :as io]))

(defn aliased-keyword-access
  [x]
  (::io/some-fake-key x))

(ns resources.non-conforming)

(require '[clojure.string :as str])

(defn using-a-different-keyword-alias-in-different-ns
  [z]
  (::str/another-fake-key z))

(in-ns 'resources.keywords)

(defn flipped-back-aliases-still-there
  [y]
  (::io/last-fake-key y))

(defn auto-namespaced-keyword [w]
  (::local-key w))
