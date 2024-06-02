(ns component.jdbc.data-source.hikari.configuration
  (:require
   [configurati.core :as conf]))

(def pool-name-parameter
  (conf/parameter :pool-name
    {:type :string :nilable true}))
(def maximum-pool-size-parameter
  (conf/parameter :maximum-pool-size
    {:type :integer :default 10}))
(def minimum-idle-parameter
  (conf/parameter :minimum-idle
    {:type :integer :default 10}))
(def idle-timeout-parameter
  (conf/parameter :idle-timeout
    {:type :integer :nilable true}))
(def connection-timeout-parameter
  (conf/parameter :connection-timeout
    {:type :integer :nilable true}))
(def maximum-lifetime-parameter
  (conf/parameter :maximum-lifetime
    {:type :integer :nilable true}))
(def auto-commit-parameter
  (conf/parameter :auto-commit
    {:type :boolean :nilable true}))

(def specification
  (conf/configuration-specification
    (conf/with-parameter pool-name-parameter)
    (conf/with-parameter maximum-pool-size-parameter)
    (conf/with-parameter minimum-idle-parameter)
    (conf/with-parameter idle-timeout-parameter)
    (conf/with-parameter connection-timeout-parameter)
    (conf/with-parameter maximum-lifetime-parameter)
    (conf/with-parameter auto-commit-parameter)))
