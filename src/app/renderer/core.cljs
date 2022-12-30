(ns app.renderer.core
  (:require
   [goog.dom :as gdom]
   [rum.core :as rum]
   [datascript.core :as d]
   [antizer.rum :as ant]
   ["react-dom/client" :refer [createRoot]]))

(enable-console-print!)

(def schema {:todo/tags    {:db/cardinality :db.cardinality/many}
             :todo/project {:db/valueType :db.type/ref}
             :todo/done    {:db/index true}
             :todo/due     {:db/index true}
             :route/handler {:db/cardinality :db.cardinality/one}
             :route/params {:db/cardinality :db.cardinality/one}})

(defonce root (createRoot (gdom/getElement "app-container")))

(defonce state (d/create-conn schema))

(rum/defc list-todos
  < rum/reactive [conn]
  (let [db (rum/react conn)
        todos (d/q '[:find ?id ?text
                     :keys id text
                     :where [?id :todo/text ?text]]
                   db)]
    [:ul {:style {:height "100%" :list-style "none"}} (map #(identity [:li {:key (:id %)} (:text %)]) todos)]))

(defn side-menu []
  (ant/menu {:mode "inline" :theme :dark :style {:height "100%"}}
            (ant/menu-item {:key "Today"} "Today")
            (ant/menu-sub-menu {:title "Projects" :key "projects"}
                               (ant/menu-item {:key "1"} "Item 1")
                               (ant/menu-item {:key 2} "Item 2"))
            (ant/menu-item {:key 3} [:span {:key "s"} "Menu Item"])))

(defn user-form []
  (ant/form {:name "basic" :label-col {:span 8} :wrapper-col {:span 16} :initial-values {:remember true} :on-finish println :on-finish-failed println}
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
             (ant/button {:type "primary" :html-type "submit"} "Submit"))))

(defn content-area []
  (ant/layout-content (list-todos state)))

(rum/defcs main-component < (rum/local false ::modal-form)
  [state]
  (let [modal-form (::modal-form state)]
    (ant/layout {:style {:height "100%"}}
                (ant/affix
                 (ant/layout-header
                  {:class "banner"}
                  (ant/row
                   (ant/col {:span 12} [:h2.banner-header {:key "layout"} "Viva Todo"])
                   (ant/col {:span 1 :offset 11}
                            (ant/button {:on-click #(reset! modal-form true)} "+")))))
                (ant/layout
                 (ant/layout-sider (side-menu))
                 (ant/layout {:style {:width "60%"}}
                             (ant/modal {:open @modal-form :title "New Todo" :width 600
                                         :on-ok #(reset! modal-form false) :on-cancel #(reset! modal-form false)}
                                        (user-form))
                             (content-area))))))

(defn  ^:dev/after-load start! []
  (.render root (main-component)))
