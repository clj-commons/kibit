(ns kibit.test.replace
  (:require [kibit.check :as check]
            [kibit.rules :as rules]
            [kibit.replace :as replace])
  (:use [clojure.test])
  (:import java.io.File
           java.io.StringWriter))

(defmacro discard-output
  "Like `with-out-str`, but discards was was written to *out*"
  [& body]
  `(binding [*out* (StringWriter.)]
     ~@body))

(deftest replace-expr-are
  (are [expected-form test-form]
      (= expected-form
         (discard-output
          (replace/replace-expr test-form)))

    '(inc a)
    '(+ 1 a)

    '(defn "Documentation" ^{:my-meta 1} [a]
       ;; a comment
       (inc a))
    '(defn "Documentation" ^{:my-meta 1} [a]
       ;; a comment
       (+ 1 a))))

(deftest replace-file-are
  (are [expected-form test-form]
      (= expected-form
         (let [file (doto (File/createTempFile "replace-file" ".clj")
                      (.deleteOnExit)
                      (spit test-form))]
           (discard-output (replace/replace-file file))
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
