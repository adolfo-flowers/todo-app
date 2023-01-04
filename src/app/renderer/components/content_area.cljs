(ns app.renderer.components.content-area
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            [app.renderer.components.todo-table :refer [todo-table]]
            [app.renderer.components.calendar :refer [calendar]]
            [app.renderer.datascript :refer [get-selected-content]]
            [app.renderer.components.list-todos :refer [list-todos]]))

(defn todos-by-status [conn project-id]
  (let [_ (println "Heyy" project-id)]
    [:div {:style {:display "flex" :padding 24} :key "content-area"}
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "To-do"]
      (list-todos conn "todo" project-id)]
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "In progress"]
      (list-todos conn "in-progress" project-id)]
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center"  :margin-bottom "10px"}} "Done"]
      (list-todos conn "done" project-id)]]))

(def content-by-key {"all" todos-by-status
                     "projects" todos-by-status
                     "calendar" calendar
                     "manage" todo-table})

(rum/defc content-area < rum/reactive
  [conn]
  (let [db (rum/react conn)
        [ck project-id] (get-selected-content db)
        _ (println "selecting content" ck)]
    (ant/layout-content
     {:style {:margin "70px 16px 0"
              :overflow "initial"}}
     ((content-by-key ck todos-by-status) conn (js/Number project-id)))))
