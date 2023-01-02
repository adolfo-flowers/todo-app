(ns app.renderer.components.create-todo
  (:require [antizer.rum :as ant]
            [rum.core :as rum]
            [datascript.core :as d]))

(defn create-todo-form [state]
  (ant/form
   {:name "basic"
    :label-col {:span 8}
    :wrapper-col {:span 10}
    :initial-values {:remember true}
    :on-finish (fn [v] (let [values (js->clj v {:keywordize-keys true})]
                         (d/transact! state [{:todo/content (:content values)
                                              :todo/title (:title values)
                                              :todo/due-date (.toISOString (:due-date values))
                                              :todo/status "todo"}])))
    :on-finish-failed println}
   (ant/form-item
    {:label "Title" :name "title" :rules (clj->js [{:required true :message "Please enter a title"}])}
    (ant/input))
   (ant/form-item
    {:label "Due Date"
     :name "due-date"
     :format "YYYY-MM-DD HH:mm"
     :rules (clj->js  [{:type "object" :whitespace true :required true :message "Please enter a due date"}])}
    (ant/date-picker {:show-time true}))
   (ant/form-item
    {:label "content" :name "content" :rules (clj->js [{:required false}])}
    (ant/input-text-area {:rows 4}))
   (ant/form-item
    {:wrapper-col {:offset 8 :span 16}}
    (ant/button {:type "primary" :html-type "submit"} "Create"))))

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
