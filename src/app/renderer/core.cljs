(ns app.renderer.core
  (:require
   [goog.dom :as gdom]
   [antizer.rum :as ant]
   [rum.core :as rum]
   [mount.core :as m]
   [app.renderer.components.side-menu :refer [side-menu]]
   [app.renderer.components.header :refer [header]]
   [app.renderer.components.content-area :refer [content-area]]
   [app.renderer.datascript :refer [conn]]
   ["react-dom/client" :refer [createRoot]]))

(enable-console-print!)
;; (rum.core/set-warn-on-interpretation! true)
(m/start)

(defonce root (createRoot (gdom/getElement "app-container")))

(rum/defc main-component
  [conn]
  (ant/layout
   {:key 1 :style {:min-height "100%"}}
   [:<> {:key 1}
    [:div.sticky {:key "hdr"}
     (header conn)]
    (ant/layout {:key 1 :has-sider true}
                (ant/layout-sider
                 {:key 1
                  :style {:overflow "auto"
                          :height "100vh"
                          :position "fixed"
                          :left 0
                          :top 63
                          :bottom 0}}
                 (side-menu conn))
                (ant/layout {:key 2 :style {:marginLeft 200}}
                            (content-area conn)))]))

(defn  ^:dev/after-load start! []
  (.render root (main-component @conn)))
