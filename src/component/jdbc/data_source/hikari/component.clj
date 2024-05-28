(ns component.jdbc.data-source.hikari.component
  (:require
   [cartus.core :as log]
   [com.stuartsierra.component :as component]
   [component.jdbc.data-source.hikari.data-sources :as data-sources])
  (:import [java.io Closeable]))

(defn- with-logging-fn [logger target opts action-fn]
  (let [init-ms (System/currentTimeMillis)]
    (when logger
      (log/debug logger (keyword (name target)
                          (name (get-in opts [:phases :before])))
        (:context opts)))
    (let [result (action-fn)]
      (when logger
        (log/info logger (keyword (name target)
                           (name (get-in opts [:phases :after])))
          (merge {:elapsed-ms (- (System/currentTimeMillis) init-ms)}
            (:context opts))))
      result)))

(defmacro ^:private with-logging [logger target opts & body]
  `(with-logging-fn ~logger ~target ~opts
     (fn [] ~@body)))

(defrecord HikariJdbcDataSource
  [configuration logger delegate datasource]
  component/Lifecycle

  (start [component]
    (with-logging logger :component.jdbc.data-source.hikari
      {:phases {:before :starting :after :started}
       :context {:configuration configuration}}
      (let [hikari-data-source
            (data-sources/hikari-data-source
              (merge configuration
                {:data-source (:datasource delegate)}))]
        (assoc component :datasource hikari-data-source))))

  (stop [component]
    (with-logging logger :component.jdbc.data-source.hikari
      {:phases  {:before :stopping :after :stopped}
       :context {:configuration configuration}}
      (let [^Closeable data-source (:datasource component)]
        (when data-source
          (.close data-source))
        (assoc component :datasource nil)))))
