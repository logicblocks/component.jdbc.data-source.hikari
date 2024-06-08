(ns component.jdbc.data-source.hikari.core
  (:require
   [component.jdbc.data-source.hikari.component :as component]))

(defn component
  ([]
   (component/map->HikariJdbcDataSource {}))
  ([{:keys [delegate
            configuration-specification
            configuration-source
            configuration-lookup-prefix
            configuration
            logger]}]
   (component/map->HikariJdbcDataSource
     {:delegate                    delegate
      :configuration-specification configuration-specification
      :configuration-source        configuration-source
      :configuration-lookup-prefix configuration-lookup-prefix
      :configuration               configuration
      :logger                      logger})))
