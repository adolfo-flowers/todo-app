(ns app.renderer.components.create-todo
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            ["moment" :as moment]
            ["antd" :refer [Form]]
            ["@ant-design/icons"  :refer [PlusOutlined]]
            [app.renderer.datascript :as d]))

(defn create-new-project-on-click [create-project set-project-name e]
  (e.preventDefault)
  (set-project-name
   (set-project-name (fn [project-name]
                       (when (not-empty project-name)
                         (create-project project-name))
                       ""))))

(rum/defc drop-down-select-project
  [create-project menu]
  (let [[project-name set-project-name] (rum/use-state "")]
    [:<>
     menu
     (ant/divider {:style {:margin "8px 0"}})
     [:div
      {:style {:padding "0 8px 4px"}}
      (ant/input {:placeholder "New project"
                  :value project-name
                  :on-change (fn [^js/e.target.value e]
                               (set-project-name (.-target.value e)))})
      (ant/button {:type "text" :on-click (partial create-new-project-on-click create-project set-project-name)}
                  "create project")]]))

(defn due-date-input []
  (ant/form-item
   {:label "Due Date"
    :name "due-date"
;;    :format "YYYY-MM-DD HH:mm"
    :rules (clj->js  [{:type "object"
                       :whitespace true
                       :required true
                       :message "Please enter a due date"}])}
   (ant/date-picker {:show-time {:defaultValue (moment "00:00:00" "HH:mm")}})))

(defn project-input [create-project projects]
  (ant/form-item
   {:label "Project"
    :name "project"
    :rules (clj->js [{:required true :message "Please enter a title"}])}
   (ant/select {:style {:max-width "300px"}
                :show-search false
                :placeholder "Select a project"
                :dropdown-render (partial drop-down-select-project create-project)
                :options (map #(clj->js {:label (:name %) :value (:name %)}) projects)})))

(defn title-input []
  (ant/form-item
   {:label "Title"
    :name "title"
    :rules (clj->js [{:required true :message "Please enter a title"}])}
   (ant/input)))

(defn status-input []
  (ant/form-item
   {:label "Status"
    :name "status"}
   (ant/select {:style {:max-width "300px"}
                :show-search false
                :placeholder "Select status"
                :options (map #(clj->js {:label % :value %})
                              ["todo" "in-progress" "done"])})))

(defn notes-input []
  (ant/form-item
   {:label "Notes" :name "notes" :rules (clj->js [{:required false}])}
   (ant/input-text-area {:rows 4})))

(defn submit-button [text]
  (ant/form-item {:wrapper-col {:offset 8 :span 10}}
                 (ant/button {:block true
                              :type "primary"
                              :html-type "submit"}
                             text)))

(rum/defc todo-form
  [projects initial-values create-todo create-project]
  (let [[form] (Form.useForm)]
    (rum/use-effect! #(form.setFieldsValue initial-values) [initial-values])
    (ant/form
     {:name "create-todo"
      :form form
      :label-col {:span 8}
      :wrapper-col {:span 10}
      :on-finish (fn [fv]
                   (let [form-values  (js->clj fv {:keywordize-keys true})
                         current-todo  (js->clj initial-values {:keywordize-keys true})
                         new-todo  (assoc form-values :id (:id current-todo -1))]
                     (if (not= current-todo new-todo)
                       (do (create-todo new-todo)
                           (form.resetFields))
                       (form.setFieldsValue initial-values))))}
     (title-input)
     (when initial-values (status-input))
     (project-input create-project projects)
     (due-date-input)
     (notes-input)
     (submit-button (if (nil? initial-values) "Create" "Update")))))

(rum/defcs create-todo-modal < rum/reactive
  [_ conn open? set-open initial-values]
  (let [db (rum/react conn)
        projects (d/get-projects db)]
    (ant/modal {:key (:id initial-values)
                :footer nil
                :open open?
                :title (if (nil? initial-values) "New Todo" "Update todo")
                :width 600
                :on-ok #(set-open false)
                :on-cancel #(set-open false)}
               (todo-form projects initial-values #(d/create-todo conn %)  #(d/create-project conn %)))))

(rum/defcs create-todo-button < rum/reactive
  [_ conn]
  (let [db (rum/react conn)
        open? (d/get-modal-state db 1)
        set-open (partial d/set-modal-state conn 1)]
    [:<>
     (create-todo-modal conn open? set-open nil)
     (ant/button {:type "primary"
                  :icon (js/React.createElement PlusOutlined)
                  :on-click #(set-open true)})]))
