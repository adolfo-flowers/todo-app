(ns app.renderer.core
  (:require
   [goog.dom :as gdom]
   [antizer.rum :as ant]
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

(defn main-component
  [conn]
  (ant/layout
   {:style {:min-height "100%"}}
   [:div.sticky {:id "hdr"}
    (header conn)]
   (ant/layout {:has-sider true}
               (ant/layout-sider
                {:style {:overflow "auto"
                         :height "100vh"
                         :position "fixed"
                         :left 0
                         :top 63
                         :bottom 0}}
                (side-menu conn))
               (ant/layout {:style {:marginLeft 200}}
                           (content-area conn)))))

(defn  ^:dev/after-load start! []
  (.render root (main-component @conn)))
