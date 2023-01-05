(ns app.renderer.components.header
  (:require
   [rum.core :as rum]
   [app.renderer.components.create-todo :refer [create-todo-button]]
   [antizer.rum :as ant]))

(rum/defc header < rum/static
  [state]
  (ant/layout-header
   {:key 1 :class "banner"}
   (ant/row {:key 1}
            (ant/col {:key 1 :span 11}
                     [:h2.banner-header
                      {:key "layout"}
                      [:img {:src "img/logo.svg"
                             :style {:height "25px"
                                     :margin-right "10px"}}] "Viva Todo"])
            (ant/col {:key 2 :span 1 :offset 11} (create-todo-button state)))))
