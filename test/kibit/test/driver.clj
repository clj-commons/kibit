(ns kibit.test.driver
  (:require [kibit.driver :as driver]
            [kibit.rules :refer [all-rules rule-map]]
            [kibit.rules.control-structures :as control]
            [clojure.test :refer :all]
            [clojure.java.io :as io]))

(deftest clojure-file-are
  (are [expected file] (= expected (driver/clojure-file? (io/file file)))
       true "test/resources/first.clj"
       true "test/resources/second.cljx"
       true "test/resources/third.cljs"
       false "test.resources/fourth.txt"))

(deftest find-clojure-sources-are
  (is (= [(io/file "test/resources/first.clj")
          (io/file "test/resources/second.cljx")
          (io/file "test/resources/third.cljs")]
         (driver/find-clojure-sources-in-dir (io/file "test/resources")))))

(deftest filtered-rules
  (is (= all-rules (driver/filtered-rules nil
                                          nil
                                          (io/file "test/resources/first.clj"))))
  (is (= nil (driver/filtered-rules :some-rules
                                    {"first.clj" #{:all}}
                                    (io/file "test/resources/first.clj"))))
  (is (= nil (driver/filtered-rules nil
                                    {"first.clj" #{:all}}
                                    (io/file "test/resources/first.clj"))))
  (is (= nil (driver/filtered-rules nil
                                    {"first.clj" :all}
                                    (io/file "test/resources/first.clj"))))
  (is (= '(:some-rules) (driver/filtered-rules [:some-rules]
                                               nil
                                               (io/file "test/resources/first.clj"))))
  (is (= '(:some-rules) (driver/filtered-rules [:some-rules]
                                               {"first.clj" #{:some-rule-set}}
                                               (io/file "test/resources/first.clj"))))
  (is (= control/rules
         (driver/filtered-rules nil
                                {"first.clj" (-> rule-map
                                                 keys
                                                 set
                                                 (disj :control-structures))}
                                (io/file "test/resources/first.clj")))))
