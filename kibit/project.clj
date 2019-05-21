(defproject gorillalabs/kibit "0.0.0"
  :plugins [[lein-monolith "1.2.0"]
            [com.roomkey/lein-v "7.1.0"]]
  :monolith/inherit true
  :middleware [leiningen.v/version-from-scm
               leiningen.v/dependency-version-from-scm
               leiningen.v/add-workspace-data]
  :description "There's a function for that!"
  :url "https://github.com/gorillalabs/kibit"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.logic "0.8.11"]
                 [org.clojure/tools.cli "0.4.2"]
                 [rewrite-clj "0.6.1"]
                 [org.clojure/tools.reader "1.3.2"]]
  :profiles {:dev {:dependencies [[lein-marginalia "0.9.1"]]
                   :resource-paths ["test/resources"]}}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :aliases {"test-all" ["do"
                        ["clean"]
                        ["test"]
                        ["clean"]
                        ["compile" ":all"]]}
  :warn-on-reflection false)
