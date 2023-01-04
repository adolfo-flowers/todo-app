(ns app.renderer.datascript
  (:require [datascript.core :as d]
            [clojure.string :refer [split]]
            [datascript.transit :as dt]))

(def schema {:project/name       {:db/cardinality :db.cardinality/one}
             :todo/tags          {:db/cardinality :db.cardinality/many}
             :todo/project       {:db/valueType :db.type/ref}
             :todo/status        {:db/index true :db/cardinality :db.cardinality/one}
             :todo/due-date      {:db/index true :db/cardinality :db.cardinality/one}
             :todo/started-date  {:db/index true :db/cardinality :db.cardinality/one}
             :todo/finished-date {:db/index true :db/cardinality :db.cardinality/one}
             :todo/title         {:db/cardinality :db.cardinality/one}
             :todo/notes         {:db/cardinality :db.cardinality/one}
             :display/content    {:db/cardinality :db.cardinality/one}})

(defonce _state (d/create-conn schema))

(defn create-todo [conn todo]
  (d/transact! conn [{:todo/notes (or (:notes todo) "")
                      :todo/title (:title todo)
                      :todo/due-date (.toISOString (:due-date todo))
                      :todo/status "todo"
                      :todo/project (:project todo)}]))

(def query-todos-by-project '{:find [?id ?title ?notes ?due-date ?status]
                              :in [$ ?status ?project-id]
                              :keys [id title notes due-date status]
                              :where
                              [[?id :todo/project ?project-id]
                               [?id :todo/title ?title]
                               [?id :todo/status ?status]
                               [?id :todo/due-date ?due-date]
                               [?id :todo/notes ?notes]]})

(def query-all-todos '{:find [?id ?title ?notes ?due-date ?status ?project-name]
                       :in [$ ?status _]
                       :keys [id title notes due-date status project]
                       :where
                       [[?id :todo/project ?project-id]
                        [?project-id :project/name ?project-name]
                        [?id :todo/title ?title]
                        [?id :todo/status ?status]
                        [?id :todo/due-date ?due-date]
                        [?id :todo/notes ?notes]]})

(defn get-todos-by-status [db status project-id]
  (let [query (if (pos? project-id) query-todos-by-project query-all-todos)]
    (d/q query db status project-id)))

(defn get-todos [db]
  (d/q '{:find [?id ?title ?notes ?due-date ?status ?project-name]
         :keys [id title notes due-date status project]
         :where
         [[?id :todo/project ?project-id]
          [?project-id :project/name ?project-name]
          [?id :todo/title ?title]
          [?id :todo/status ?status]
          [?id :todo/due-date ?due-date]
          [?id :todo/notes ?notes]]} db))

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

(defn get-todos-by-date [db date]
  [])

(defn select-content [conn content]
  (d/transact! conn [{:db/id 1 :display/content content}]))

(defn get-selected-content [db]
  (split (:display/content  (d/pull db '[:display/content] 1)) #"-"))

(def local-storage-db-key "viva-todo-db")

(defn persist [db]
  (js/localStorage.setItem local-storage-db-key (dt/write-transit-str db)))

(d/listen! _state :persistence
           (fn [tx-report] ;; FIXME do not notify with nil as db-report
                  ;; FIXME do not notify if tx-data is empty
             (when-let [db (:db-after tx-report)]
               (persist db))))

(when-let [stored (js/localStorage.getItem local-storage-db-key)]
  (let [stored-db (dt/read-transit-str stored)]
    (when (= (:schema stored-db) schema) ;; check for code update
      (reset! _state stored-db))))
