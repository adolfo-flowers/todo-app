(ns app.renderer.components.content-area
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            [app.renderer.components.todo-table :refer [todo-table]]
            [app.renderer.components.calendar :refer [calendar]]
            [app.renderer.datascript :refer [get-selected-content get-todos-by-status]]
            [app.renderer.components.list-todos :refer [list-todos]]))

(rum/defc todos-by-status < rum/static  rum/reactive
  [conn project-id]
  (let [db (rum/react conn)
        todo-todos (get-todos-by-status db "todo"  project-id)
        in-prgrs-todos (get-todos-by-status db "in-progress" project-id)
        done-todos (get-todos-by-status db "done"  project-id)]
    [:div {:style {:display "flex" :padding 24} :key "content-area"}
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "To-do"]
      (list-todos conn todo-todos)]
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "In progress"]
      (list-todos conn in-prgrs-todos)]
     [:div
      {:style {:flex "1 30%" :padding "5px"}}
      [:h1 {:style {:text-align "center"  :margin-bottom "10px"}} "Done"]
      (list-todos conn done-todos)]]))

(def content-by-key {"all" todos-by-status
                     "projects" todos-by-status
                     "calendar" calendar
                     "manage" todo-table})

(rum/defc content-area < rum/static  rum/reactive
  [conn]
  (let [db (rum/react conn)
        [ck project-id] (get-selected-content db)]
    (ant/layout-content
     {:style {:margin "70px 16px 0"
              :overflow "initial"}}
     ((content-by-key ck todos-by-status) conn (js/Number project-id)))))
