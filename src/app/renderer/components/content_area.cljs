(ns app.renderer.components.content-area
  (:require [antizer.rum :as ant]
            [app.renderer.components.list-todos :refer [list-todos]]))

(defn content-area [state]
  (ant/layout-content
   {:style {:margin "0px 16px 0"
            :overflow "initial"}}
   [:div {:style {:display "flex" :padding 24} :key "content-area"}
    [:div
     {:style {:flex "1 30%" :padding "5px"}}
     [:h1 {:style {:text-align "center"}} "To-do"]
     (list-todos state)]
    [:div
     {:style {:flex "1 30%" :padding "5px"}}
     [:h1 {:style {:text-align "center"}} "In progress"]
     (list-todos state)]
    [:div
     {:style {:flex "1 30%" :padding "5px"}}
     [:h1 {:style {:text-align "center"}} "Done"]
     (list-todos state)]]))
