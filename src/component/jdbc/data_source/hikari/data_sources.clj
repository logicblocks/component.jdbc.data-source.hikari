(ns component.jdbc.data-source.hikari.data-sources
  (:import
   [com.zaxxer.hikari HikariConfig HikariDataSource]))

(defn hikari-data-source
  [{:keys [data-source
           pool-name
           maximum-pool-size
           minimum-idle
           idle-timeout
           connection-timeout
           maximum-lifetime
           auto-commit]}]
  (let [hc (HikariConfig.)]
    (when (some? data-source)
      (.setDataSource hc data-source))
    (when (some? pool-name)
      (.setPoolName hc pool-name))
    (when (some? maximum-pool-size)
      (.setMaximumPoolSize hc (int maximum-pool-size)))
    (when (some? minimum-idle)
      (.setMinimumIdle hc (int minimum-idle)))
    (when (some? idle-timeout)
      (.setIdleTimeout hc (int idle-timeout)))
    (when (some? connection-timeout)
      (.setConnectionTimeout hc (long connection-timeout)))
    (when (some? maximum-lifetime)
      (.setMaxLifetime hc (long maximum-lifetime)))
    (when (some? auto-commit)
      (.setAutoCommit hc auto-commit))
    (HikariDataSource. hc)))
