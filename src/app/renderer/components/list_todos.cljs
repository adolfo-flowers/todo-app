(ns app.renderer.components.list-todos
  (:require
   [app.renderer.datascript :refer [update-todo-status]]
   [antizer.rum :as ant]
   [rum.core :as rum]
   ["@ant-design/icons" :refer [ClockCircleOutlined]]
   ["moment" :as moment]))

(def state-transitions {"todo" "in-progress" "in-progress" "done" "done" "todo"})

(defn transition-button [text on-click]
  (ant/button
   {:danger true
    :block true
    :on-click on-click}
   text))

(defn todo-block-header [todo]
  [:div {:style {:display "flex"
                 :justify-content "space-between"
                 :align-items "baseline"
                 :gap "10px"}}
   [:h3 {:style {:max-width "140px"
                 :white-space "nowrap"
                 :margin-bottom 0
                 :overflow "hidden"
                 :text-overflow "ellipsis"}}
    (:title todo)]
   (ant/tag {:color "warning"
             :icon (js/React.createElement ClockCircleOutlined)
             :style {:flex "0 1 10%"}}
            (.fromNow (moment (:due-date todo))))])

(defn format-date [ts]
  (.format (moment ts) "llll"))

(defn todo-block [button todo]
  (ant/collapse-panel
   {:key (:id todo)
    :show-arrow true
    :header (todo-block-header todo)}
   (ant/card
    {:key (:id todo)
     :title (str "Project: " (:project todo))
     :style {:max-width "295px" :overflow "hidden"}}
    (str "Due: " (format-date (:due-date todo)))
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (str "Title: " (:title todo))
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (:notes todo)
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (button))))

(rum/defc list-todos < rum/static
  [conn todos]
  (ant/collapse
   (map (fn [todo]
          (let [next-todo-status (state-transitions (:status todo))
                on-click #(update-todo-status conn (:id todo) next-todo-status)]
            (todo-block #(transition-button next-todo-status on-click) todo)))
        todos)))
