(defproject jonase/kibit (clojure.string/trim-newline (slurp "resources/jonase/kibit/VERSION"))
  :description "There's a function for that!"
  :url "https://github.com/clj-commons/kibit"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :scm {:name "git"
        :url "https://github.com/clj-commons/kibit"
        :connection "scm:git:git://github.com/clj-commons/kibit.git"
        :developerConnection "scm:git:ssh://git@github.com/clj-commons/kibit.git"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.logic "1.1.0"]
                 [org.clojure/tools.cli "1.1.230"]
                 [rewrite-clj "1.1.47"]
                 [org.clojure/tools.reader "1.4.2"]]
  :profiles {:dev {:resource-paths ["test/resources"]}}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :sign-releases false}]
                        ["snapshots" :clojars]]
  :aliases {"test-all" ["do"
                        ["clean"]
                        ["test"]
                        ["clean"]
                        ["compile" ":all"]]})
