(ns kibit.test.driver
  (:require [kibit.driver :as driver]
            [clojure.test :refer :all]
            [clojure.java.io :as io])
  (:import (java.io ByteArrayOutputStream PrintWriter)))

(deftest clojure-file-are
  (are [expected file] (= expected (driver/clojure-file? (io/file file)))
       true "test/resources/first.clj"
       true "test/resources/second.cljx"
       true "test/resources/third.cljs"
       false "test.resources/fourth.txt"))

(deftest find-clojure-sources-are
  (is (= [(io/file "test/resources/first.clj")
          (io/file "test/resources/keywords.clj")
          (io/file "test/resources/second.cljx")
          (io/file "test/resources/sets.clj")
          (io/file "test/resources/third.cljs")]
         (driver/find-clojure-sources-in-dir (io/file "test/resources")))))

(deftest test-set-file
  (is (driver/run ["test/resources/sets.clj"] nil)))

(deftest test-keywords-file
  (let [test-buf (ByteArrayOutputStream.)
        test-err (PrintWriter. test-buf)]
    (binding [*err* test-err]
      (driver/run ["test/resources/keywords.clj"] nil))
    (is (zero? (.size test-buf))
        (format "Test err buffer contained '%s'" (.toString test-buf)))))
