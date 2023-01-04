(ns app.renderer.components.side-menu
  (:require
   [app.renderer.datascript :refer [get-projects select-content]]
   [rum.core :as rum]
   [antizer.rum :as ant]))

(defn select-content-display [conn e]
  (let [content (.join (.reverse (.-keyPath e)) "-")
        _ (println "saving display" content)]
    (select-content conn content)))

(defn list-projects [conn projects]
  (map #(ant/menu-item
         {:on-click (partial select-content-display conn)
          :key (:id %)}
         (:name %))
       projects))

(rum/defc side-menu < rum/reactive
  [conn]
  (let [db (rum/react conn)
        projects (get-projects db)]
    (ant/menu {:mode "inline" :theme :dark}
              (ant/menu-item {:on-click (partial select-content-display conn) :key "all"}
                             "All")
              (ant/menu-item {:on-click (partial select-content-display conn)  :key "calendar"}
                             "Calendar")
              (ant/menu-sub-menu {:title "Projects" :key "projects" :disabled (empty? projects)}
                                 (list-projects conn projects))
              (ant/menu-item {:key "manage-todos"}
                             "Manage Todos")
              (ant/menu-item {:key "stats"}
                             "Stats"))))
