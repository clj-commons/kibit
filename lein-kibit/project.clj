(defproject lein-kibit (clojure.string/trim-newline (slurp "../resources/jonase/kibit/VERSION"))
  :description "kibit lein plugin"
  :url "https://github.com/clj-commons/kibit"
  :resource-paths ["resources"]
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/tools.namespace "1.5.0"]
                 [jonase/kibit ~(clojure.string/trim-newline (slurp "../resources/jonase/kibit/VERSION"))]]
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :sign-releases false}]
                        ["snapshots" :clojars]]
  :eval-in-leiningen true)
