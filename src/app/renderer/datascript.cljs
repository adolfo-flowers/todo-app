(ns app.renderer.datascript
  (:require [datascript.core :as d]
            [clojure.string :refer [split]]
            [mount.core :as m]
            [datascript.transit :as dt]))

(def schema {:project/name       {:db/unique :db.unique/value :db/cardinality :db.cardinality/one}
             :todo/tags          {:db/cardinality :db.cardinality/many}
             :todo/project       {:db/valueType :db.type/ref}
             :todo/status        {:db/index true :db/cardinality :db.cardinality/one}
             :todo/due-date      {:db/index true :db/cardinality :db.cardinality/one}
             :todo/started-date  {:db/index true :db/cardinality :db.cardinality/one}
             :todo/finished-date {:db/index true :db/cardinality :db.cardinality/one}
             :todo/title         {:db/cardinality :db.cardinality/one}
             :todo/notes         {:db/cardinality :db.cardinality/one}
             :display/content    {:db/cardinality :db.cardinality/one}
             :modal/open  {:db/cardinality :db.cardinality/one}
             :modal/todo {:db/unique :db.unique/value}})

(defn create-todo [conn todo]
  (d/transact! conn [{:db/id (or (:id todo) -1)
                      :todo/notes (or (:notes todo) "")
                      :todo/title (:title todo)
                      :todo/due-date (.toISOString (:due-date todo))
                      :todo/status (or (:status todo) "todo")
                      :todo/project [:project/name (:project todo)]}]))

(defn get-todos [db]
  (d/q '{:find [?id ?title ?notes ?due-date ?status ?project-name]
         :keys [id title notes due-date status project]
         :where
         [[?id :todo/project ?project-id]
          [?project-id :project/name ?project-name]
          [?id :todo/title ?title]
          [?id :todo/status ?status]
          [?id :todo/due-date ?due-date]
          [?id :todo/notes ?notes]]}
       db))

(defn get-todos-by-project-id [db project-id]
  (d/q '{:find [?id ?title ?notes ?due-date ?status ?project-name]
         :in [$ ?project-id]
         :keys [id title notes due-date status project]
         :where
         [[?id :todo/project ?project-id]
          [?project-id :project/name ?project-name]
          [?id :todo/title ?title]
          [?id :todo/status ?status]
          [?id :todo/due-date ?due-date]
          [?id :todo/notes ?notes]]}
       db project-id))

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

(defn same-dateTime? [^js/moment.isSame calendar-date todo-date]
  (let [check (fn [t] (.isSame calendar-date todo-date t))]
    (and (check "year") (check "month") (check "day"))))

(defn get-todos-by-date [db date]
  (let [todos (d/q '{:find [?id ?title ?notes ?due-date ?status ?project-name]
                     :in [$ ?date ?same-dateTime?]
                     :keys [id title notes due-date status project]
                     :where
                     [[?id :todo/project ?project-id]
                      [?project-id :project/name ?project-name]
                      [?id :todo/title ?title]
                      [?id :todo/status ?status]
                      [?id :todo/due-date ?due-date]
                      [(?same-dateTime? ?date ?due-date)]
                      [?id :todo/notes ?notes]]} db date same-dateTime?)]
    todos))

(defn select-content [conn content]
  (d/transact! conn [{:db/id 1 :display/content content}]))

(defn get-selected-content [db]
  (split (:display/content  (d/pull db '[:display/content] 1)) #"-"))

(defn get-modal-state [db id]
  (:modal/open  (d/pull db '[:modal/open] id)))

(defn set-modal-state [conn id state]
  (d/transact! conn [{:db/id id :modal/open state}]))

(defn get-modal-todo [db]
  (let [todos (d/q '{:find [?id ?title ?notes ?due-date ?status ?project-name]
                     :keys [id title notes due-date status project]
                     :where
                     [[_ :modal/todo ?id]
                      [?id :todo/project ?project-id]
                      [?project-id :project/name ?project-name]
                      [?id :todo/title ?title]
                      [?id :todo/status ?status]
                      [?id :todo/due-date ?due-date]
                      [?id :todo/notes ?notes]]} db)]
    todos))

(defn set-modal-todo [conn todo]
  (d/transact! conn [{:db/id 1 :modal/todo todo}]))

(def local-storage-db-key "viva-todo-db")

(defn persist [db]
  (js/localStorage.setItem local-storage-db-key (dt/write-transit-str db)))

(defn load-db-from-ls [conn]
  (when-let [stored (js/localStorage.getItem local-storage-db-key)]
    (let [stored-db (dt/read-transit-str stored)]
      (when (= (:schema stored-db) schema) ;; check for code update
        (reset! conn stored-db)))))

(defn save-on-every-transaction [conn]
  (d/listen! conn :persistence
             (fn [tx-report]
               (when-let [db (:db-after tx-report)]
                 (persist db)))))

(defn init-db []
  (let [conn (d/create-conn schema)]
    (load-db-from-ls conn)
    (save-on-every-transaction conn)
    conn))

(m/defstate conn :start (init-db))
