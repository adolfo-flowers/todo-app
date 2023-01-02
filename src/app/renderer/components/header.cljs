(ns app.renderer.components.header
  (:require
   [app.renderer.components.create-todo :refer [create-todo-button]]
   [antizer.rum :as ant]))

(defn header
  [state]
  (ant/layout-header
   {:class "banner"}
   (ant/row
    (ant/col {:span 12} [:h2.banner-header {:key "layout"} "Viva Todo"])
    (ant/col {:span 1 :offset 11} (create-todo-button state)))))
