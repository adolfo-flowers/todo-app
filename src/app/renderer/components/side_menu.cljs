(ns app.renderer.components.side-menu
  (:require
   [rum.core :as rum]
   [datascript.core :as d]
   [antizer.rum :as ant]))

(defn side-menu []
  (ant/menu {:mode "inline" :theme :dark}
            (ant/menu-item {:key "Today"} "Today")
            (ant/menu-sub-menu {:title "Projects" :key "projects"}
                               (ant/menu-item {:key "1"} "Item 1")
                               (ant/menu-item {:key 2} "Item 2"))
            (ant/menu-item {:key 3} [:span {:key "s"} "Menu Item"])))
