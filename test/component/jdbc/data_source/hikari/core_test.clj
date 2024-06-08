(ns component.jdbc.data-source.hikari.core-test
  (:require
   [clojure.test :refer :all]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.hikari.configuration :as configuration]
   [component.jdbc.data-source.hikari.core :as hikari-component]
   [configurati.component :as conf-comp]
   [configurati.core :as conf])
  (:import
   [com.zaxxer.hikari HikariDataSource]
   [org.postgresql.ds PGSimpleDataSource]
   [java.util.concurrent TimeUnit]))

(defn delegate-data-source []
  (let [delegate (PGSimpleDataSource.)]
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
  (let [delegate {:datasource (delegate-data-source)}
        configuration {}]
    (with-started-component
      (hikari-component/component
        {:configuration configuration
         :delegate      delegate})
      (fn [component]
        (let [^HikariDataSource data-source (:datasource component)]
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
  (let [delegate {:datasource (delegate-data-source)}
        configuration
        {:pool-name          "main"
         :maximum-pool-size  15
         :minimum-idle       10
         :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
         :connection-timeout (.toMillis TimeUnit/SECONDS 20)
         :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
         :auto-commit        false}]
    (with-started-component
      (hikari-component/component
        {:configuration configuration
         :delegate      delegate})
      (fn [component]
        (let [^HikariDataSource data-source (:datasource component)]
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

(deftest configures-component-using-default-specification
  (let [delegate {:datasource (delegate-data-source)}
        configuration
        {:pool-name          "main"
         :maximum-pool-size  15
         :minimum-idle       10
         :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
         :connection-timeout (.toMillis TimeUnit/SECONDS 20)
         :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
         :auto-commit        false}
        component (hikari-component/component
                    {:delegate delegate})
        component (conf-comp/configure component
                    {:configuration-source (conf/map-source configuration)})]
    (is (= configuration (:configuration component)))))

(deftest allows-specification-to-be-overridden
  (let [delegate {:datasource (delegate-data-source)}
        configuration
        {:maximum-pool-size 15
         :minimum-idle      10}
        specification
        (conf/configuration-specification
          (conf/with-parameter :pool-name :default "application-pool")
          (conf/with-parameter configuration/maximum-pool-size-parameter)
          (conf/with-parameter configuration/minimum-idle-parameter))
        component (hikari-component/component
                    {:delegate                    delegate
                     :configuration-specification specification})
        component (conf-comp/configure component
                    {:configuration-source (conf/map-source configuration)})]
    (is (= {:pool-name         "application-pool"
            :maximum-pool-size 15
            :minimum-idle      10}
          (:configuration component)))))

(deftest allows-default-source-to-be-provided
  (let [delegate {:datasource (delegate-data-source)}
        default-source
        (conf/map-source
          {:pool-name         "main"
           :maximum-pool-size 15
           :minimum-idle      10
           :auto-commit       false})
        configure-time-source
        (conf/map-source
          {:maximum-pool-size  20
           :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
           :connection-timeout (.toMillis TimeUnit/SECONDS 20)
           :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)})
        component (hikari-component/component
                    {:delegate             delegate
                     :configuration-source default-source})
        component (conf-comp/configure component
                    {:configuration-source configure-time-source})]
    (is (= {:pool-name          "main"
            :maximum-pool-size  20
            :minimum-idle       10
            :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
            :connection-timeout (.toMillis TimeUnit/SECONDS 20)
            :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
            :auto-commit        false}
          (:configuration component)))))

(deftest allows-configuration-lookup-key-to-be-provided
  (let [delegate {:datasource (delegate-data-source)}
        configuration-source
        (conf/map-source
          {:connection-pool-pool-name          "main"
           :connection-pool-maximum-pool-size  15
           :connection-pool-minimum-idle       10
           :connection-pool-idle-timeout       (.toMillis TimeUnit/MINUTES 15)
           :connection-pool-connection-timeout (.toMillis TimeUnit/SECONDS 20)
           :connection-pool-maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
           :connection-pool-auto-commit        false})
        component (hikari-component/component
                    {:delegate                    delegate
                     :configuration-lookup-prefix :connection-pool})
        component (conf-comp/configure component
                    {:configuration-source configuration-source})]
    (is (= {:pool-name          "main"
            :maximum-pool-size  15
            :minimum-idle       10
            :idle-timeout       (.toMillis TimeUnit/MINUTES 15)
            :connection-timeout (.toMillis TimeUnit/SECONDS 20)
            :maximum-lifetime   (.toMillis TimeUnit/MINUTES 20)
            :auto-commit        false}
          (:configuration component)))))
