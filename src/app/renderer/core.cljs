(ns app.renderer.core
  (:require
   [goog.dom :as gdom]
   [rum.core :as rum]
   [antizer.rum :as ant]
   [app.renderer.datascript :refer [schema]]
   [app.renderer.components.side-menu :refer [side-menu]]
   [app.renderer.components.header :refer [header]]
   [app.renderer.components.content-area :refer [content-area]]
   [app.renderer.datascript :refer [_state]]
   ["react-dom/client" :refer [createRoot]]))

(enable-console-print!)
(rum.core/set-warn-on-interpretation! true)

(defonce root (createRoot (gdom/getElement "app-container")))

(defn main-component
  [state]
  (ant/layout
   (ant/affix (ant/layout (header state)))
   (ant/layout {:has-sider true}
               (ant/layout-sider
                {:style {:overflow "auto"
                         :height "100vh"
                         :position "fixed"
                         :left 0
                         :top 63
                         :bottom 0}}
                (side-menu))
               (ant/layout {:style {:marginLeft 200}}
                           (content-area state)))))

(defn  ^:dev/after-load start! []
  (.render root (main-component _state)))
