(ns app.main.core
  (:require ["electron" :refer [app BrowserWindow]]))

(defn create-window []
  (let [win (BrowserWindow.
             (clj->js {:width 800
                       :height 600}))]
    (.loadFile win (str js/__dirname "/public/index.html"))))

(defn init-browser []
  (create-window)
  (.on app "activate" #(when-not (.-length (.getAllWindows BrowserWindow))
                         (create-window))))

(defn main []
  (.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                  (.quit app)))
  (.then (.whenReady app)  init-browser))
