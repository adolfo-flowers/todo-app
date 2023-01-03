(ns app.renderer.components.create-todo
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            ["moment" :as moment]
            [app.renderer.datascript :refer [get-projects create-project]]
            [app.renderer.datascript :refer [create-todo]]))

(defn create-todo-from-values [conn v]
  (let [todo (js->clj v {:keywordize-keys true})]
    (println todo)
    (create-todo conn todo)))

(rum/defcs drop-down-select-project < (rum/local [] ::new-project)
  [local-state projects conn menu]
  (let [new-project (local-state ::new-project)]
    [:<>
     menu
     (ant/divider {:style {:margin "8px 0"}})
     [:div
      {:style {:padding "0 8px 4px"}}
      (ant/input {:placeholder "New project"
                  :value @new-project
                  :on-change (fn [^js/event.target.value e] (reset! new-project (.-target.value e)))})
      (ant/button {:type "text" :on-click  (fn [e]
                                             (e.preventDefault)
                                             (create-project conn @new-project)
                                             (reset! new-project ""))} "+")]]))

(rum/defcs create-todo-form < rum/reactive
  [local-state conn]
  (let [db (rum/react conn)
        projects (get-projects db)]
    (ant/form
     {:name "create-todo"
      :label-col {:span 8}
      :wrapper-col {:span 10}
    ;;:initial-values {:remember true}
      :on-finish (partial create-todo-from-values conn)
      :on-finish-failed println}
     (ant/form-item
      {:label "Title"
       :name "title"
       :rules (clj->js [{:required true :message "Please enter a title"}])}
      (ant/input))
     (ant/form-item
      {:label "Project"
       :name "project"
       :rules (clj->js [{:required true :message "Please enter a title"}])}
      (ant/select {:style {:width "300px"}
                   :show-search true
                   :placeholder "Select a project"
                   :dropdown-render (partial drop-down-select-project projects conn)
                   :options (map #(clj->js {:label (:name %) :value (:id %)}) projects)}))
     (ant/form-item
      {:label "Due Date"
       :name "due-date"
       :format "YYYY-MM-DD HH:mm"
       :rules (clj->js  [{:type "object"
                          :whitespace true
                          :required true
                          :message "Please enter a due date"}])}
      (ant/date-picker {:show-time {:defaultValue (moment "00:00:00" "HH:mm")}}))
     (ant/form-item
      {:label "content" :name "content" :rules (clj->js [{:required false}])}
      (ant/input-text-area {:rows 4}))
     (ant/form-item
      {:wrapper-col {:offset 8 :span 16}}
      (ant/button {:type "primary" :html-type "submit"} "Create")))))

(rum/defcs create-todo-button < (rum/local false ::modal-form)
  [local-state state]
  (let [modal-form (::modal-form local-state)]
    [(ant/modal {:key 1
                 :footer nil
                 :open @modal-form
                 :title "New Todo"
                 :width 600
                 :on-ok #(reset! modal-form false)
                 :on-cancel #(reset! modal-form false)}
                (create-todo-form state))
     (ant/button {:key 2 :on-click #(reset! modal-form true)} "+")]))
