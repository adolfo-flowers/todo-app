(ns app.renderer.components.content-area
  (:require [antizer.rum :as ant]
            [app.renderer.components.list-todos :refer [list-todos]]))

(defn todos-by-status [conn]
  [:div {:style {:display "flex" :padding 24} :key "content-area"}
   [:div
    {:style {:flex "1 30%" :padding "5px"}}
    [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "To-do"]
    (list-todos conn "todo")]
   [:div
    {:style {:flex "1 30%" :padding "5px"}}
    [:h1 {:style {:text-align "center" :margin-bottom "10px"}} "In progress"]
    (list-todos conn "in-progress")]
   [:div
    {:style {:flex "1 30%" :padding "5px"}}
    [:h1 {:style {:text-align "center"  :margin-bottom "10px"}} "Done"]
    (list-todos conn "done")]])

(defn content-area [conn]
  (ant/layout-content
   {:style {:margin "0px 16px 0"
            :overflow "initial"}}
   (todos-by-status conn)))
