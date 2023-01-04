(ns app.renderer.components.todo-table
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            [app.renderer.datascript :refer [get-todos]]))

(def columns [{:title "Title" :dataIndex "title"}
              {:title "Due date" :dataIndex "due-date"}
              {:title "Project" :dataIndex "project"}
              {:title "status" :dataIndex "status"}])

(rum/defc todo-table  < rum/reactive
  [conn]
  (let [db (rum/react conn)
        todos (map #(merge % {:key (:id %)}) (get-todos db))]
    (ant/table {:columns (clj->js columns) :data-source (clj->js todos) :on-change println})))
