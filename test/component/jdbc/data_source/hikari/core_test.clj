(ns component.jdbc.data-source.hikari.core-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.hikari.core :as hikari-component])
  (:import
   [com.zaxxer.hikari HikariDataSource]
   [org.postgresql.ds PGSimpleDataSource]
   [java.util.concurrent TimeUnit]))

(defn delegate-data-source []
  (let [delegate (org.postgresql.ds.PGSimpleDataSource.)]
    (doto delegate
      (.setServerNames (into-array String ["localhost"]))
      (.setPortNumbers (int-array [5433]))
      (.setUser "admin")
      (.setPassword "super-secret")
      (.setDatabaseName "some-database"))
    delegate))

(defn with-started-component [component f]
  (let [container (atom component)]
    (try
      (do
        (swap! container component/start)
        (f @container))
      (finally
        (swap! container component/stop)))))

(deftest creates-hikari-data-source-with-default-parameters
  (let [delegate (delegate-data-source)
        configuration {}]
    (with-started-component
      (hikari-component/hikari-jdbc-data-source configuration delegate)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)]
          (is (= 10 (.getMaximumPoolSize data-source)))
          (is (= 10 (.getMinimumIdle data-source)))
          (is (= (.toMillis TimeUnit/MINUTES 10)
                (.getIdleTimeout data-source)))
          (is (= (.toMillis TimeUnit/SECONDS 30)
                (.getConnectionTimeout data-source)))
          (is (= (.toMillis TimeUnit/MINUTES 30)
                (.getMaxLifetime data-source)))
          (is (not (nil? (.getPoolName data-source))))
          (is (true? (.isAutoCommit data-source))))))))

(deftest uses-provided-configuration-for-hikaru-data-source
  (let [delegate (delegate-data-source)
        configuration
        {:pool-name          "main"
         :maximum-pool-size  15
         :minimum-idle       10
         :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
         :connection-timeout (.toMillis TimeUnit/SECONDS 20)
         :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
         :auto-commit        false}]
    (with-started-component
      (hikari-component/hikari-jdbc-data-source configuration delegate)
      (fn [component]
        (let [^HikariDataSource data-source (:data-source component)]
          (is (= 15 (.getMaximumPoolSize data-source)))
          (is (= 10 (.getMinimumIdle data-source)))
          (is (= (.toMillis TimeUnit/MINUTES 15)
                (.getIdleTimeout data-source)))
          (is (= (.toMillis TimeUnit/SECONDS 20)
                (.getConnectionTimeout data-source)))
          (is (= (.toMillis TimeUnit/MINUTES 20)
                (.getMaxLifetime data-source)))
          (is (= "main" (.getPoolName data-source)))
          (is (false? (.isAutoCommit data-source))))))))
