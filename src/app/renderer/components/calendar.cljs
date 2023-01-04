(ns app.renderer.components.calendar
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            [app.renderer.datascript :refer [get-todos-by-date]]))

(rum/defc render-cell < rum/reactive
  [conn date]
  (let [db (rum/react conn)
        todos (get-todos-by-date db date)]
    [:ul {:class-name "events" :style {:list-style "none"}}
     (map (fn [todo] [:li {:key (:id todo)}
                      (ant/badge {:status "error" :text (:title todo)})])
          todos)]))

(defn calendar
  [conn _]
  (ant/calendar {:date-cell-render (partial render-cell conn)}))
