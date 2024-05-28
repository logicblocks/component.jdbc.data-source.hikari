# component.jdbc.data-source.hikari

[![Clojars Project](https://img.shields.io/clojars/v/io.logicblocks/component.jdbc.data-source.hikari.svg)](https://clojars.org/io.logicblocks/component.jdbc.data-source.hikari)
[![Clojars Downloads](https://img.shields.io/clojars/dt/io.logicblocks/component.jdbc.data-source.hikari.svg)](https://clojars.org/io.logicblocks/component.jdbc.data-source.hikari)
[![GitHub Contributors](https://img.shields.io/github/contributors-anon/logicblocks/component.jdbc.data-source.hikari.svg)](https://github.com/logicblocks/component.jdbc.data-source.hikari/graphs/contributors)

A component providing a HikariCP JDBC data source.

## Install

Add the following to your `project.clj` file:

```clj
[io.logicblocks/component.jdbc.data-source.hikari "0.1.2"]
```

## Documentation

* [API Docs](https://logicblocks.github.io/component.jdbc.data-source.hikari/index.html)

## Usage

```clojure
(require '[com.stuartsierra.component :as component])
(require '[component.jdbc.data-source.hikari.core 
            :as hikari-jdbc-data-source])

(def system
  (component/system-map
    :hikari-data-source 
    (hikari-jdbc-data-source/create
      {:host "localhost"
       :port 5432
       :user "admin"
       :password "super-secret-password"
       :database-name "test"})))
```

## License

Copyright &copy; 2024 LogicBlocks Maintainers

Distributed under the terms of the 
[MIT License](http://opensource.org/licenses/MIT).
