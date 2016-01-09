(defproject jonase/kibit "0.1.2"
  :description "There's a function for that!"
  :url "https://github.com/jonase/kibit"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.logic "0.8.10"]
                 [org.clojure/tools.cli "0.3.3"]]
  :profiles {:dev {:dependencies [[lein-marginalia "0.8.0"]]
                   :resource-paths ["test/resources"]}}
  :deploy-repositories [["releases" :clojars]]
  :warn-on-reflection false)
