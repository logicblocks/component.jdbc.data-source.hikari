(ns component.jdbc.data-source.hikari.core
  (:require
   [component.jdbc.data-source.hikari.component :as component]))

(defn hikari-jdbc-data-source
  ([]
   (component/map->HikariJdbcDataSource {}))
  ([configuration delegate]
   (component/map->HikariJdbcDataSource
     {:configuration configuration
      :delegate      delegate})))
