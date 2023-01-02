(ns app.renderer.datascript
  (:require [datascript.core :as d]))

(def schema {:todo/tags     {:db/cardinality :db.cardinality/many}
             :todo/project  {:db/valueType :db.type/ref}
             :todo/done     {:db/index true}
             :todo/due-date {:db/index true}
             :todo/title    {:db/cardinality :db.cardinality/one}
             :todo/content    {:db/cardinality :db.cardinality/one}
             :route/handler {:db/cardinality :db.cardinality/one}
             :route/params  {:db/cardinality :db.cardinality/one}})

(defonce _state (d/create-conn schema))
