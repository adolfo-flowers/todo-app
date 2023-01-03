(ns app.renderer.components.list-todos
  (:require   [rum.core :as rum]
              [app.renderer.datascript :refer [get-todos-by-status update-todo-status]]
              [antizer.rum :as ant]
              ["moment" :as moment]))

(def state-transitions {"todo" "in-progress" "in-progress" "done" "done" "todo"})

(defn transition-button [conn id new-todo-status]
  (ant/button
   {:style {:margin-left "10px"}
    :on-click (fn [] (update-todo-status conn id new-todo-status))}
   new-todo-status))

(defn todo-block-header [conn todo]
  [:div
   [:h3 (:title todo)
    [:span {:style {:margin "0 0 0 5px"}} (.fromNow (moment (:due-date todo)))]
    (transition-button conn (:id todo) (state-transitions (:status todo)))]])

(defn todo-block [conn todo]
  (ant/collapse-panel
   {:key (:id todo)
    :header (todo-block-header conn todo)}
   [:p
    {:key (:id todo)}
    (:content todo)]))

(rum/defc list-todos < rum/reactive
  [conn todo-status]
  (let [db (rum/react conn)
        todos (get-todos-by-status db todo-status)]
    (ant/collapse
     (map (partial todo-block conn) todos))))
