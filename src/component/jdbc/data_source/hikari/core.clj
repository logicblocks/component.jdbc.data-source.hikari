(ns component.jdbc.data-source.hikari.core
  (:require
   [component.jdbc.data-source.hikari.component :as component]))

(defn component
  ([]
   (component/map->HikariJdbcDataSource {}))
  ([{:keys [delegate
            configuration-specification
            configuration-source
            configuration
            logger]}]
   (component/map->HikariJdbcDataSource
     {:delegate                    delegate
      :configuration-specification configuration-specification
      :configuration-source        configuration-source
      :configuration               configuration
      :logger                      logger})))
