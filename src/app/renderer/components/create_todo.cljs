(ns app.renderer.components.create-todo
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            ["moment" :as moment]
            ["antd" :refer [Form]]
            ["@ant-design/icons"  :refer [PlusOutlined QuestionCircleOutlined]]
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
    :rules (clj->js [{:required true :message "Please select a project"}])}
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
  [projects todo-to-update create-todo create-project delete-todo]
  (let [[form] (Form.useForm)]
    (rum/use-effect! #(form.setFieldsValue (clj->js todo-to-update)) [todo-to-update])
    (ant/form
     {:name "create-todo"
      :form form
      :label-col {:span 8}
      :wrapper-col {:span 10}
      :on-finish (fn [fv]
                   (let [form-values  (js->clj fv {:keywordize-keys true})
                         new-todo  (assoc form-values :id (:id todo-to-update -1))]
                     (if (not= todo-to-update new-todo)
                       (do (create-todo new-todo)
                           (ant/message-success (str (:title new-todo) " created"))
                           (form.resetFields))
                       (form.setFieldsValue (clj->js todo-to-update)))))}
     (title-input)
     (when todo-to-update
       (status-input))
     (project-input create-project projects)
     (due-date-input)
     (notes-input)
     (submit-button (if (nil? todo-to-update) "Create" "Update"))
     (when-not (nil? todo-to-update)
       (ant/form-item {:wrapper-col {:offset 8 :span 10}}
                      (ant/popconfirm {:title "Are you sure you want to delete this todo?"
                                       :icon (js/React.createElement QuestionCircleOutlined)
                                       :style {:color "red"}
                                       :on-confirm delete-todo}
                                      (ant/button {:danger true
                                                   :block true
                                                   :type "primary"}
                                                  "delete")))))))

(rum/defcs create-todo-modal < rum/reactive
  [_ conn open? set-open todo-to-update]
  (let [db (rum/react conn)
        projects (d/get-projects db)]
    (ant/modal {:key (:id todo-to-update "new-todo")
                :footer nil
                :open open?
                :title (if (nil? todo-to-update)
                         "New Todo"
                         "Update todo")
                :width 600
                :on-ok #(set-open false)
                :on-cancel #(set-open false)}
               (todo-form projects
                          todo-to-update
                          #(d/create-todo conn %)
                          #(d/create-project conn %)
                          (fn []
                            (d/delete-todo conn (:id todo-to-update))
                            (d/set-modal-state conn 3 false))))))

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
