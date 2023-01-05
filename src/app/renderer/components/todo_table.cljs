(ns app.renderer.components.todo-table
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            ["moment" :as moment]
            [app.renderer.components.create-todo :refer [create-todo-modal]]
            [app.renderer.datascript :refer [get-todos
                                             get-projects
                                             get-modal-todo
                                             set-modal-todo
                                             get-modal-state
                                             set-modal-state]]))

(defn columns [pfilters]
  [{:title "Title" :dataIndex "title"}
   {:title "Due date"
    :dataIndex "due-date"
    :defaultSortOrder "descend"
    :sorter  (fn [a b] (let [ca ((js->clj a)  "ts")
                             cb ((js->clj b)  "ts")]
                         (- (.format (moment ca) "YYYYMMDD") (.format (moment cb) "YYYYMMDD"))))}
   {:title "Project"
    :dataIndex "project"
    :filters pfilters
    :filterSearch true
    :onFilter (fn [v r] (.includes r.project v))}
   {:title "status" :dataIndex "status"}])

(defn format-date [ts]
  (.format (moment ts) "llll"))

(rum/defc todo-table  < rum/reactive
  [conn]
  (let [db (rum/react conn)
        todos (map #(merge % {:key (:id %) :due-date (format-date (:due-date %)) :ts (:due-date %)}) (get-todos db))
        projects (get-projects db)
        pfilters (map (fn [{:keys [name]}] {:text name :value name}) projects)
        initial-values (clj->js (update (first (get-modal-todo db)) :due-date #(moment %)))
        open (get-modal-state db 3)
        set-open (partial set-modal-state conn 3)]
    [(create-todo-modal conn open set-open initial-values)
     (ant/table {:key 5
                 :row-class-name (constantly "table-row")
                 :columns (clj->js (columns pfilters))
                 :on-row (fn [record] (clj->js {:onClick (fn [e]
                                                           (e.preventDefault)
                                                           (set-modal-todo conn ((js->clj record) "id"))
                                                           (set-open true))}))
                 :data-source (clj->js todos)})]))
