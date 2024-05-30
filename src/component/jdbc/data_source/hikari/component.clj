(ns component.jdbc.data-source.hikari.component
  (:require
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.hikari.data-sources :as data-sources]
   [component.support.logging :as comp-log])
  (:import [java.io Closeable]))

(defrecord HikariJdbcDataSource
  [configuration logger delegate datasource]
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
