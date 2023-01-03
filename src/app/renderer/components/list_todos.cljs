(ns app.renderer.components.list-todos
  (:require   [rum.core :as rum]
              [app.renderer.datascript :refer [get-todos-by-status update-todo-status]]
              [antizer.rum :as ant]
              ["@ant-design/icons" :refer [ClockCircleOutlined]]
              ["moment" :as moment]))

(def state-transitions {"todo" "in-progress" "in-progress" "done" "done" "todo"})
(println ClockCircleOutlined)
(defn transition-button [conn id new-todo-status]
  (ant/button
   {:style {:margin-left "10px"}
    :on-click (fn [] (update-todo-status conn id new-todo-status))}
   new-todo-status))

(defn todo-block-header [conn todo]
  [:div {:style {:display "flex" :justify-content "center"}}
   [:h3 {:style {:flex "1"}} (:title todo)
    (ant/tag {:color "warning" :icon (js/React.createElement ClockCircleOutlined) :style {:float "right"}} (.fromNow (moment (:due-date todo))))]])

(defn todo-block [conn todo]
  (ant/collapse-panel
   {:key (:id todo)
    :header (todo-block-header conn todo)}
   [:p
    {:key (:id todo)}
    (:notes todo)]))

(rum/defc list-todos < rum/reactive
  [conn todo-status]
  (let [db (rum/react conn)
        todos (get-todos-by-status db todo-status)]
    (ant/collapse
     (map (partial todo-block conn) todos))))
