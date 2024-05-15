(ns kibit.driver
  "The (leiningen) facing interface for Kibit. Provides helpers for finding files in a project, and
  linting a list of files."
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :refer [cli]]
            [kibit.check :refer [check-file]]
            [kibit.replace :refer [replace-file]]
            [kibit.reporters :refer [name-to-reporter cli-reporter]]
            [kibit.rules :refer [all-rules]])
  (:import java.io.File))

(def cli-specs [["-r" "--reporter"
                 "The reporter used when rendering suggestions"
                 :default "text"]
                ["-e" "--replace"
                 "Automatically apply suggestions to source file"
                 :flag true]
                ["-i" "--interactive"
                 "Interactively prompt before replacing suggestions in source file (Requires `--replace`)"
                 :flag true]])

(defn ends-with?
  "Returns true if the java.io.File ends in any of the strings in coll"
  [file coll]
  (boolean (some #(.endsWith (.getName ^File file) %) coll)))

(defn clojure-file?
  "Returns true if the java.io.File represents a Clojure source file.
  Extensions taken from https://github.com/github/linguist/blob/master/lib/linguist/languages.yml"
  [file]
  (and (.isFile ^File file)
       (ends-with? file [".clj" ".cl2" ".cljc" ".cljs" ".cljscm" ".cljx" ".hic" ".hl"])))

(defn find-clojure-sources-in-dir
  "Searches recursively under dir for Clojure source files.
  Returns a sequence of File objects, in breadth-first sort order.
  Taken from clojure.tools.namespace.find"
  [^File dir]
  ;; Use sort by absolute path to get breadth-first search.
  (sort-by #(.getAbsolutePath ^File %)
           (filter clojure-file? (file-seq dir))))

(defn- run-replace [source-files rules options]
  (doseq [file source-files]
    (replace-file file
                  :rules (or rules all-rules)
                  :interactive (:interactive options))))

(defn- run-check [source-files rules {:keys [reporter]}]
  (mapcat (fn [file] (try (check-file file
                                      :reporter (name-to-reporter reporter
                                                                  cli-reporter)
                                      :rules (or rules all-rules))
                          (catch Exception e
                            (let [e-info (ex-data e)]
                              (binding [*out* *err*]
                                (println (format "Check failed -- skipping rest of file (%s:%s:%s)"
                                                 (.getPath ^File file)
                                                 (:line e-info)
                                                 (:column e-info)))
                                (println (.getMessage e)))))))
          source-files))

(defn run
  "Runs the kibit checker against the given paths, rules and args.

  Paths is expected to be a sequence of io.File objects.

  Rules is either a collection of rules or nil. If rules is nil, all of kibit's checkers are used.

  Optionally accepts a :reporter keyword argument, defaulting to \"text\"
  If :replace is provided in options, suggested replacements will be performed automatically."
  [source-paths rules & args]
  (let [[options file-args usage-text] (apply (partial cli args) cli-specs)
        source-files                   (mapcat #(-> % io/file find-clojure-sources-in-dir)
                                               (if (empty? file-args) source-paths file-args))]
    (if (:replace options)
      (run-replace source-files rules options)
      (run-check source-files rules options))))

(defn external-run
  "Used by lein-kibit to count the results and exit with exit-code 1 if results are found"
  [source-paths rules & args]
  (if (zero? (count (apply run source-paths rules args)))
    (System/exit 0)
    (System/exit 1)))

(defn exec
  "Given a [Clojure CLI-style map][Execute a function] `options`, turn this map
  into an equivalent set of options as expected by run and external-run above.

  Please note that rules is not supported. nil is passed to external-run below
  which enables all rules (see kibit.rules/all-rules).

  **DO NOT** escape the dobule-quotes in deps.edn or on the command-line.

  To make use of this, add an alias, e.g. kibit, to deps.edn:

      :kibit {:extra-deps {jonase/kibit {:mvn/version \"0.1.10\"}}
              :exec-fn kibit.driver/exec
              :exec-args {:paths [\"src\" \"test\"]}}

  Then run:

      clojure -X:kibit

  Additional command-line options can be added in deps.edn or on the
  command-line. For example, in deps.edn:

      :kibit {:extra-deps {jonase/kibit {:mvn/version \"0.1.10\"}}
              :exec-fn kibit.driver/exec
              :exec-args {:paths [\"src\" \"test\"]
                          :interactive true}}

  Or on the command-line:

      clojure -X:kibit :interactive true

  To use [babashka.cli][babashka.cli], update the kibit alias in deps.edn:

      :kibit {:extra-deps {jonase/kibit {:mvn/version \"0.1.10\"}}
                           org.babashka/cli {:mvn/version \"0.7.51\"}}
              :exec-fn kibit.driver/exec
              :exec-args {:paths [\"src\" \"test\"]}
              :main-opts [\"-m\" \"babashka.cli.exec\"]}

  Then run:

      clojure -X:kibit -i -r markdown

  [Execute a function]: https://clojure.org/reference/deps_and_cli#_execute_a_function
  [babashka.cli]: https://github.com/babashka/cli
  "
  {:org.babashka/cli {:alias {:r :reporter
                              :e :replace
                              :i :interactive}
                      :coerce {:paths [:string]
                               :reporter :string
                               :replace :boolean
                               :interactive :boolean}}}
  [{:keys [paths reporter replace interactive] :as _options}]
  (apply (partial external-run paths nil) ["-r" (or reporter "text")
                                           (when replace "-e")
                                           (when interactive "-i")]))
