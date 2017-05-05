(defproject lein-kibit (clojure.string/trim-newline (slurp "../kibit-common/resources/jonase/kibit/VERSION"))
  :description "kibit lein plugin"
  :url "https://github.com/jonase/lein-kibit"
  :resource-paths ["../kibit-common/resources"]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/tools.namespace "0.2.11"]
                 [jonase/kibit ~(clojure.string/trim-newline (slurp "../kibit-common/resources/jonase/kibit/VERSION"))]]
  :deploy-repositories [["releases" :clojars]]
  :eval-in-leiningen true)
