(ns kibit.test.driver
  (:require [kibit.driver :as driver]
            [clojure.test :refer [deftest is are]]
            [clojure.java.io :as io])
  (:import (java.io ByteArrayOutputStream PrintWriter)))

(deftest clojure-file-are
  (are [expected file] (= expected (driver/clojure-file? (io/file file)))
       true "corpus/first.clj"
       true "corpus/second.cljx"
       true "corpus/third.cljs"
       false "corpus/fourth.txt"))

(deftest find-clojure-sources-are
  (is (= [(io/file "corpus/as_alias.clj")
          (io/file "corpus/double_pound_reader_macros.clj")
          (io/file "corpus/first.clj")
          (io/file "corpus/keyword_suggestions.clj")
          (io/file "corpus/keywords.clj")
          (io/file "corpus/namespaced_maps.clj")
          (io/file "corpus/read_eval.clj")
          (io/file "corpus/reader_conditionals.cljc")
          (io/file "corpus/second.cljx")
          (io/file "corpus/sets.clj")
          (io/file "corpus/third.cljs")]
         (driver/find-clojure-sources-in-dir (io/file "corpus")))))

(deftest test-set-file
  (is (driver/run ["corpus/sets.clj"] nil)))

(deftest test-keywords-file
  (let [test-buf (ByteArrayOutputStream.)
        test-err (PrintWriter. test-buf)]
    (binding [*err* test-err]
      (driver/run ["corpus/keywords.clj"] nil))
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
              (driver/run ["corpus/keyword_suggestions.clj"] nil "--reporter" "no-op")))))

(defmacro with-err-str
  [& body]
  `(let [s# (java.io.StringWriter.)]
     (binding [*err* s#]
       ~@body
       (str s#))))

(deftest process-reader-macros
  (is (= ["" "" "" ""]
         [(with-err-str
            (driver/run ["corpus/reader_conditionals.cljc"] nil "--reporter" "no-op"))
          (with-err-str
            (driver/run ["corpus/double_pound_reader_macros.clj"] nil "--reporter" "no-op"))
          (with-err-str
            (driver/run ["corpus/namespaced_maps.clj"] nil "--reporter" "no-op"))
          (with-err-str
            (driver/run ["corpus/as_alias.clj"] nil "--reporter" "no-op"))])))

(deftest no-read-eval-test
  (is (= [{:expr
           '(if true (do (edamame.core/read-eval (prn :hello :world!)) :a))
           :line 1
           :column 1
           :end-line 1
           :end-column 41
           :alt '(when true (edamame.core/read-eval (prn :hello :world!)) :a)}]
         (map #(dissoc % :file)
              (driver/run ["corpus/read_eval.clj"] nil "--reporter" "no-op")))))
