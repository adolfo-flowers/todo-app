(ns app.renderer.components.list-todos
  (:require   [rum.core :as rum]
              [datascript.core :as d]
              [antizer.rum :as ant]))

(rum/defc list-todos
  < rum/reactive [conn]
  (let [db (rum/react conn)
        todos (d/q '[:find ?id ?title ?content
                     :keys id title content
                     :where
                     [?id :todo/title ?title]
                     [?id :todo/content ?content]]
                   db)]
    (ant/collapse
     (map (fn [todo] (ant/collapse-panel {:key (:id todo) :header [:h3 (:title todo)]} [:p (:content todo)])) todos))))
