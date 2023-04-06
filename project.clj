(defproject jonase/kibit (clojure.string/trim-newline (slurp "resources/jonase/kibit/VERSION"))
  :description "There's a function for that!"
  :url "https://github.com/jonase/kibit"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.logic "1.0.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [rewrite-clj "1.1.47"]
                 [org.clojure/tools.reader "1.3.6"]]
  :profiles {:dev {:dependencies [[lein-marginalia "0.9.0"]]
                   :resource-paths ["test/resources"]}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :aliases {"test-all" ["do"
                        ["clean"]
                        ["test"]
                        ["clean"]
                        ["compile" ":all"]]})
