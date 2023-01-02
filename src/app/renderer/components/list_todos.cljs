(ns app.renderer.components.list-todos
  (:require   [rum.core :as rum]
              [datascript.core :as d]
              [antizer.rum :as ant]))

(def state-transitions {"todo" "in-progress" "in-progress" "done" "done" "todo"})

(defn transition-button [status conn id]
  (ant/button
   {:style {:margin-left "10px"}
    :on-click (fn []
                (d/transact! conn [{:db/id id :todo/status (state-transitions status)}]))}
   (state-transitions status)))

(rum/defc list-todos < rum/reactive
  [conn status]
  (let [db (rum/react conn)
        todos (d/q '[:find ?id ?title ?content ?due-date
                     :in $ ?status
                     :keys id title content due-date
                     :where
                     [?id :todo/title ?title]
                     [?id :todo/status ?status]
                     [?id :todo/due-date ?due-date]
                     [?id :todo/content ?content]]
                   db status)]
    (ant/collapse
     (map (fn [todo]
            (ant/collapse-panel
             {:key (:id todo)
              :header [:div
                       [:h3 (:title todo)
                        [:span {:style {:margin "0 0 0 5px"}} (:due-date todo)]
                        (transition-button status conn (:id todo))]]}
             [:p
              {:key (:id todo)}
              (:content todo)]))
          todos))))
