(ns app.renderer.datascript
  (:require [datascript.core :as d]))

(def schema {:project/name  {:db/cardinality :db.cardinality/one}
             :todo/tags     {:db/cardinality :db.cardinality/many}
             :todo/project  {:db/valueType :db.type/ref}
             :todo/status   {:db/cardinality :db.cardinality/one}
             :todo/due-date {:db/index true}
             :todo/title    {:db/cardinality :db.cardinality/one}
             :todo/notes    {:db/cardinality :db.cardinality/one}
             :route/handler {:db/cardinality :db.cardinality/one}
             :route/params  {:db/cardinality :db.cardinality/one}})

(defonce _state (d/create-conn schema))

(defn create-todo [conn todo]
  (d/transact! conn [{:todo/notes (:notes todo)
                      :todo/title (:title todo)
                      :todo/due-date (.toISOString (:due-date todo))
                      :todo/status "todo"
                      :todo/project (:project todo)}]))

(defn get-todos-by-status [db status]
  (d/q '[:find ?id ?title ?notes ?due-date ?status
         :in $ ?status
         :keys id title notes due-date status
         :where
         [?id :todo/title ?title]
         [?id :todo/status ?status]
         [?id :todo/due-date ?due-date]
         [?id :todo/notes ?notes]]
       db status))

(defn update-todo-status [conn id status]
  (d/transact! conn [{:db/id id :todo/status status}]))

(defn create-project [conn project]
  (d/transact! conn [{:project/name project}]))

(defn get-projects [db]
  (d/q '[:find ?id ?name
         :keys id name
         :where
         [?id :project/name ?name]]
       db))
