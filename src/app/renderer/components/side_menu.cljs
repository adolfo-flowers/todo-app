(ns app.renderer.components.side-menu
  (:require
   [app.renderer.datascript :refer [get-projects select-content]]
   [rum.core :as rum]
   ["@ant-design/icons" :refer [CalendarOutlined ProjectOutlined HomeOutlined EditOutlined]]
   [antizer.rum :as ant]))

(defn select-content-display [conn e]
  (let [content (.join (.reverse (.-keyPath e)) "-")]
    (select-content conn content)))

(defn list-projects [on-click projects]
  (map #(ant/menu-item
         {:on-click on-click
          :key (:id %)}
         (:name %))
       projects))

(rum/defc side-menu < rum/static  rum/reactive
  [conn]
  (let [db (rum/react conn)
        projects (get-projects db)]
    (ant/menu {:mode "inline" :theme :dark}
              (ant/menu-item {:icon (js/React.createElement HomeOutlined)
                              :on-click (partial select-content-display conn)
                              :key "all"}
                             "All")
              (ant/menu-item {:icon (js/React.createElement CalendarOutlined)
                              :on-click (partial select-content-display conn)
                              :key "calendar"}
                             "Calendar")
              (ant/menu-sub-menu {:icon (js/React.createElement ProjectOutlined)
                                  :title "Projects"
                                  :key "projects"
                                  :disabled (empty? projects)}
                                 (list-projects #(select-content-display conn %) projects))
              (ant/menu-item {:icon (js/React.createElement EditOutlined)
                              :on-click (partial select-content-display conn)
                              :key "manage-todos"}
                             "Manage Todos"))))
