(defproject io.logicblocks/component.jdbc.data-source.hikari "0.1.3-RC14"
  :description "A component providing a HikariCP JDBC data source."
  :url "https://github.com/logicblocks/component.jdbc.data-source.hikari"

  :license {:name "The MIT License"
            :url  "https://opensource.org/licenses/MIT"}

  :plugins
  [[lein-cloverage "1.2.4"]
   [lein-shell "0.5.0"]
   [lein-ancient "0.7.0"]
   [lein-changelog "0.3.2"]
   [lein-cprint "1.3.3"]
   [lein-eftest "0.6.0"]
   [lein-codox "0.10.8"]
   [lein-cljfmt "0.9.2"]
   [lein-kibit "0.1.8"]
   [lein-bikeshed "0.5.2"]
   [jonase/eastwood "1.4.0"]]

  :dependencies
  [[io.logicblocks/cartus.core "0.1.18"]
   [io.logicblocks/component.support "0.1.0-RC2"]
   [io.logicblocks/configurati "0.5.7-RC16"]
   [com.stuartsierra/component "1.1.0"]
   [hikari-cp "3.1.0"]]

  :profiles
  {:shared
   ^{:pom-scope :test}
   {:dependencies
    [[org.clojure/clojure "1.11.3"]
     [org.postgresql/postgresql "42.7.3"]
     [nrepl "1.1.2"]
     [eftest "0.6.0"]]}

   :dev-specific
   {:source-paths ["dev"]
    :eftest       {:multithread? false}}

   :test-specific
   {:eftest {:multithread? false}}

   :dev
   [:shared :dev-specific]

   :test
   [:shared :test-specific]

   :prerelease
   {:release-tasks
    [["shell" "git" "diff" "--exit-code"]
     ["change" "version" "leiningen.release/bump-version" "rc"]
     ["change" "version" "leiningen.release/bump-version" "release"]
     ["vcs" "commit" "Pre-release version %s [skip ci]"]
     ["vcs" "tag"]
     ["deploy"]]}

   :release
   {:release-tasks
    [["shell" "git" "diff" "--exit-code"]
     ["change" "version" "leiningen.release/bump-version" "release"]
     ["codox"]
     ["changelog" "release"]
     ["shell" "sed" "-E" "-i.bak" "s/\"[0-9]+\\.[0-9]+\\.[0-9]+\"/\"${:version}\"/g" "README.md"]
     ["shell" "rm" "-f" "README.md.bak"]
     ["shell" "git" "add" "."]
     ["vcs" "commit" "Release version %s [skip ci]"]
     ["vcs" "tag"]
     ["deploy"]
     ["change" "version" "leiningen.release/bump-version" "patch"]
     ["change" "version" "leiningen.release/bump-version" "rc"]
     ["change" "version" "leiningen.release/bump-version" "release"]
     ["vcs" "commit" "Pre-release version %s [skip ci]"]
     ["vcs" "tag"]
     ["vcs" "push"]]}}

  :cloverage
  {:ns-exclude-regex [#"^user"]}

  :codox
  {:namespaces  [#"^component\.jdbc\.data-source\.postgres\."]
   :metadata    {:doc/format :markdown}
   :output-path "docs"
   :doc-paths   ["docs"]
   :source-uri  "https://github.com/logicblocks/component.jdbc.data-source.postgres/blob/{version}/{filepath}#L{line}"}

  :cljfmt {:indents {#".*"     [[:inner 0]]
                     defrecord [[:block 1] [:inner 1]]
                     deftype   [[:block 1] [:inner 1]]}}

  :eastwood {:config-files ["config/linter.clj"]}

  :deploy-repositories
  {"releases"  {:url "https://repo.clojars.org" :creds :gpg}
   "snapshots" {:url "https://repo.clojars.org" :creds :gpg}})
