(ns app.renderer.components.create-todo
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            ["moment" :as moment]
            ["antd" :refer [Form]]
            [app.renderer.datascript :refer [get-projects create-project create-todo get-modal-state set-modal-state]]))

(defn create-todo-from-values [conn todo]
  (create-todo conn todo))

(rum/defcs drop-down-select-project < (rum/local [] ::new-project)
  [local-state conn menu]
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
                                             (when (not-empty @new-project) (create-project conn @new-project)
                                                   (reset! new-project "")))} "create project")]]))

(rum/defcs create-todo-form < rum/reactive
  [local-state conn form initial-values]
  (let [db (rum/react conn)
        projects (get-projects db)]
    (ant/form
     {:name "create-todo"
      :form form
      :label-col {:span 8}
      :wrapper-col {:span 10}
    ;;:initial-values {:remember true}
      :on-finish (fn [v]
                   (let [values  (js->clj v {:keywordize-keys true})
                         iv  (js->clj initial-values {:keywordize-keys true})
                         new-todo (if (nil? initial-values)
                                    values
                                    (merge {:id (:id iv)} values))]
                     (create-todo-from-values conn new-todo))
                   (form.resetFields))
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
                   :dropdown-render (partial drop-down-select-project conn)
                   :options (map #(clj->js {:label (:name %) :value (:name %)}) projects)}))
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
      {:label "Notes" :name "notes" :rules (clj->js [{:required false}])}
      (ant/input-text-area {:rows 4}))
     (ant/form-item
      {:wrapper-col {:offset 8 :span 16}}
      (ant/button {:type "primary" :html-type "submit"} "Create")))))

(rum/defc create-todo-modal
  [conn open set-open initial-values]
  (let [[form] (Form.useForm)
        _ (form.setFieldsValue initial-values)]
    (ant/modal {:key (:id initial-values)
                :footer nil
                :open open
                :title (if (nil? initial-values) "New Todo" "Update todo")
                :width 600
                :on-ok #(set-open false)
                :on-cancel #(set-open false)}
               (create-todo-form conn form initial-values))))

(rum/defcs create-todo-button < rum/reactive
  [_ conn]
  (let [db (rum/react conn)
        open (get-modal-state db 1)
        set-open (partial set-modal-state conn 1)]
    [(create-todo-modal conn open set-open nil)
     (ant/button {:key 2 :on-click #(set-open true)} "+")]))
