(ns app.renderer.components.header
  (:require
   [app.renderer.components.create-todo :refer [create-todo-button]]
   [antizer.rum :as ant]))

(defn header
  [state]
  (ant/layout-header
   {:class "banner"}
   (ant/row
    (ant/col {:span 11}
             [:h2.banner-header
              {:key "layout"}
              [:img {:src "img/logo.svg"
                     :style {:height "25px"
                             :margin-right "10px"}}] "Viva Todo"])
    (ant/col {:span 1 :offset 11} (create-todo-button state)))))
