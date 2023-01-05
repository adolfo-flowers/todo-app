(ns app.renderer.components.list-todos
  (:require   [rum.core :as rum]
              [app.renderer.datascript :refer [get-todos-by-status update-todo-status]]
              [antizer.rum :as ant]
              ["@ant-design/icons" :refer [ClockCircleOutlined EyeOutlined]]
              ["moment" :as moment]))

(def state-transitions {"todo" "in-progress" "in-progress" "done" "done" "todo"})

(defn transition-button [conn id new-todo-status]
  (ant/button
   {:danger true
    :block true
    :on-click (fn [] (update-todo-status conn id new-todo-status))}
   new-todo-status))

(defn todo-block-header [todo]
  [:div {:style {:display "flex"
                 :justify-content "space-between"
                 :align-items "baseline"
                 :gap "10px"}}
   [:h3 {:style {:width "200px"
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

(defn todo-block [conn todo]
  (ant/collapse-panel
   {:key (:id todo)
    :show-arrow false
    :header (todo-block-header todo)}
   (ant/card
    {:key (:id todo) :title (:project todo) :style {:width "300px" :overflow "hidden"}}
    (format-date (:due-date todo))
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (:title todo)
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (:notes todo)
    (ant/divider {:style {:margin "20px 0 15px 0"}})
    (transition-button conn (:id todo) (state-transitions (:status todo))))))

(rum/defc list-todos < rum/reactive
  [conn todo-status project-id]
  (let [db (rum/react conn)
        todos (get-todos-by-status db todo-status project-id)]
    (ant/collapse
     (map (partial todo-block conn) todos))))
