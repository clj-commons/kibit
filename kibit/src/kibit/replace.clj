(ns kibit.replace
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [rewrite-clj.zip :as rewrite.zip]
            [rewrite-clj.node :as rewrite.node]
            [kibit.check :as check]
            [kibit.reporters :as reporters]))

(defn- prompt
  "Create a yes/no prompt using the given message.

  From `leiningen.ancient.console`."
  [& msg]
  (let [msg (str (str/join msg) " [yes/no] ")]
    (locking *out*
      (loop [i 0]
        (when (= (mod i 4) 2)
          (println "*** please type in one of 'yes'/'y' or 'no'/'n' ***"))
        (print msg)
        (flush)
        (let [r (or (read-line) "")
              r (.toLowerCase ^String r)]
          (case r
            ("yes" "y") true
            ("no" "n")  false
            (recur (inc i))))))))

(defn- report-or-prompt
  ""
  [file interactive? {:keys [line expr alt]}]
  (if interactive?
    (prompt (with-out-str
              (println "Would you like to replace")
              (reporters/pprint-code expr)
              (println " with")
              (reporters/pprint-code alt)
              (print (format "in %s:%s?" file line))))
    (do
      (println "Replacing")
      (reporters/pprint-code expr)
      (println " with")
      (reporters/pprint-code alt)
      (println (format "in %s:%s" file line))

      true)))

(def ^:private expr? (comp not rewrite.node/printable-only? rewrite.zip/node))

(defn- map-zipper
  "Apply `f` to all code forms in `zipper0`"
  [f zipper0]
  (let [zipper (if (expr? zipper0)
                 (rewrite.zip/postwalk zipper0
                                       expr?
                                       f)
                 zipper0)]
    (if (rewrite.zip/rightmost? zipper)
      zipper
      (recur f (rewrite.zip/right zipper)))))

(defn- replace-zipper*
  ""
  [zipper reporter kw-opts]
  (if-let [check-map (apply check/check-expr
                            (rewrite.zip/sexpr zipper)
                            :resolution
                            :subform
                            kw-opts)]
    (if (reporter (assoc check-map
                         :line
                         (-> zipper rewrite.zip/node meta :row)))
      (recur (rewrite.zip/edit zipper
                               (fn -replace-zipper [sexpr]
                                 (let [alt (:alt check-map)]
                                   (if (meta alt)
                                     (vary-meta alt
                                                (fn -remove-loc [m]
                                                  (dissoc m
                                                          :line
                                                          :column)))
                                     alt))))
             reporter
             kw-opts)
      zipper)
    zipper))

(defn- replace-zipper
  ""
  [zipper & kw-opts]
  (let [options (apply hash-map kw-opts)]
    ;; TODO use (:reporter options) to determine format?
    (replace-zipper* zipper
                     (partial report-or-prompt
                              (:file options)
                              (:interactive options))
                     kw-opts)))

(defn replace-expr
  "Apply any suggestions to `expr`.

  `expr`    - Code form to check and replace in
  `kw-opts` - any valid option for `check/check-expr`, as well as:
              - `:file` current filename
              - `:interactive` prompt for confirmation before replacement or not

  Returns a string of the replaced form"
  [expr & kw-opts]
  (->> (str expr)
       rewrite.zip/of-string
       (map-zipper (fn -replace-expr [node]
                     (apply replace-zipper
                            node
                            kw-opts)))
       rewrite.zip/root
       rewrite.node/sexpr))

(defn replace-file
  "Apply any suggestions to `file`.

  `file`    - File to check and replace in
  `kw-opts` - any valid option for `check/check-expr`, as well as:
              - `:interactive` prompt for confirmation before replacement or not

  Modifies `file`, returns `nil`"
  [file & kw-opts]
  (->> (slurp file)
       rewrite.zip/of-string
       (map-zipper (fn -replace-zipper [node]
                     (apply replace-zipper
                            node
                            :file (str file)
                            kw-opts)))
       rewrite.zip/root-string
       (spit file)))
