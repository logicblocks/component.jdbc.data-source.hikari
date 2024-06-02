(ns component.jdbc.data-source.hikari.component
  (:require
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.hikari.configuration :as configuration]
   [component.jdbc.data-source.hikari.data-sources :as data-sources]
   [component.support.logging :as comp-log]
   [configurati.component :as conf-comp]
   [configurati.core :as conf])
  (:import [java.io Closeable]))

(defrecord HikariJdbcDataSource
  [configuration-specification
   configuration-source
   configuration
   logger
   delegate
   datasource]

  conf-comp/Configurable
  (configure [component opts]
    (comp-log/with-logging logger :component.jdbc.data-source.hikari
      {:phases  {:before :configuring :after :configured}}
      (assoc component
        :configuration
        (conf/resolve
          (conf/configuration
            (conf/with-specification
              (or configuration-specification configuration/specification))
            (conf/with-source
              (apply conf/multi-source
                (remove nil?
                  [(:configuration-source opts)
                   configuration-source]))))))))

  component/Lifecycle
  (start [component]
    (comp-log/with-logging logger :component.jdbc.data-source.hikari
      {:phases {:before :starting :after :started}
       :context {:configuration configuration}}
      (let [hikari-data-source
            (data-sources/hikari-data-source
              (merge configuration
                {:data-source (:datasource delegate)}))]
        (assoc component :datasource hikari-data-source))))

  (stop [component]
    (comp-log/with-logging logger :component.jdbc.data-source.hikari
      {:phases  {:before :stopping :after :stopped}
       :context {:configuration configuration}}
      (let [^Closeable data-source (:datasource component)]
        (when data-source
          (.close data-source))
        (assoc component :datasource nil)))))
