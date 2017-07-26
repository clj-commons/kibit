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
          (io/file "test/resources/keyword_suggestions.clj")
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

(deftest test-keyword-suggestions-file
  (is (= '({:alt  (vec [:clojure.java.io/some-fake-key :resources.keyword-suggestions/local-key :some/other-key])
            :expr (into [] [:clojure.java.io/some-fake-key :resources.keyword-suggestions/local-key :some/other-key])}
            {:alt  (vec [:clojure.string/another-fake-key :resources.non-conforming/local-key2 :some/other-key2])
             :expr (into [] [:clojure.string/another-fake-key :resources.non-conforming/local-key2 :some/other-key2])}
            {:alt  (vec [:clojure.java.io/last-fake-key :resources.keyword-suggestions/local-key3 :some/other-key3])
             :expr (into [] [:clojure.java.io/last-fake-key :resources.keyword-suggestions/local-key3 :some/other-key3])}
            {:alt  (vec [:clojure.pprint/printing-key :resources.keyword-suggestions/local-key4 :some/other-key4])
             :expr (into [] [:clojure.pprint/printing-key :resources.keyword-suggestions/local-key4 :some/other-key4])})
         (map #(select-keys % [:expr :alt])
              (driver/run ["test/resources/keyword_suggestions.clj"] nil "--reporter" "no-op")))))
