(defproject lein-kibit (clojure.string/trim-newline (slurp "kibit-common/resources/jonase/kibit/VERSION"))
  :monolith/inherit true
  :middleware [leiningen.v/version-from-scm
               leiningen.v/dependency-version-from-scm
               leiningen.v/add-workspace-data]
  :description "kibit lein plugin"
  :url "https://github.com/jonase/kibit"
  :resource-paths ["../kibit-common/resources"]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/tools.namespace "0.2.11"]
                 [gorillalabs.jonase/kibit ~(clojure.string/trim-newline (slurp "kibit-common/resources/jonase/kibit/VERSION"))]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :eval-in-leiningen true)
