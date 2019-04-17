(defproject gorillalabs/kibit "0.0.0"
  :description "There's a function for that!"
  :url "https://github.com/gorillalabs/kibit"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "Contact if any questions"}
  :plugins [[lein-monolith "1.2.0"]
            [com.roomkey/lein-v "7.1.0"]]
  :middleware [leiningen.v/version-from-scm
               leiningen.v/dependency-version-from-scm
               leiningen.v/add-workspace-data]
  :monolith {:inherit [:plugins]
             :project-dirs ["*"]}
  :aliases {"cache-version" ["do" ["v" "cache" "lein-kibit/resources" "edn"]]}
  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"] ;; compute new version & tag it
                  ["v" "push-tags"]
                  ["cache-version"]
                  ["monolith" "each" "deploy" "clojars"]])