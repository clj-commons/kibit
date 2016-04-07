(ns kibit.test.replace
  (:require [kibit.check :as check]
            [kibit.rules :as rules]
            [kibit.replace :as replace])
  (:use [clojure.test])
  (:import java.io.File))

(deftest replace-file-are
  (are [expected-form test-form]
      (= expected-form
         (let [file (doto (File/createTempFile "replace-file" ".clj")
                      (.deleteOnExit)
                      (spit test-form))]
           (with-out-str (replace/replace-file file))
           (slurp file)))

    "(inc a)"
    "(+ 1 a)"

    "(ns replace-file)

     (defn \"Documentation\" ^{:my-meta 1} [a]
       ;; a comment
       (inc a))"
    "(ns replace-file)

     (defn \"Documentation\" ^{:my-meta 1} [a]
       ;; a comment
       (+ 1 a))"))
