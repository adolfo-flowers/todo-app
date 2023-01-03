(ns app.renderer.components.side-menu
  (:require
   [app.renderer.datascript :refer [get-projects]]
   [rum.core :as rum]
   [antizer.rum :as ant]))

(rum/defc side-menu < rum/reactive
  [conn]
  (let [db (rum/react conn)
        projects (get-projects db)]
    (ant/menu {:mode "inline" :theme :dark}
              (ant/menu-item {:key "Today"} "Today")
              (ant/menu-item {:key "Calendar"} [:span {:key "calendar"} "Calendar"])
              (ant/menu-sub-menu {:title "Projects" :key "projects"}
                                 (map #(ant/menu-item {:key (:id %)} (:name %)) projects))
              (ant/menu-item {:key "manage-todos"} [:span {:key 1} "Manage Todos"])
              (ant/menu-item {:key "stats"} [:span {:key 1} "Stats"]))))
