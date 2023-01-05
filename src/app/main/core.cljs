(ns app.main.core
  (:require ["electron" :refer [app BrowserWindow Notification]]
            [app.renderer.datascript :refer [local-storage-db-key]]))

(defn create-window []
  (let [win (BrowserWindow.
             (clj->js {:width 800
                       :height 600
                       :autoHideMenuBar true}))]
    (.loadFile win (str js/__dirname "/public/index.html"))))

(def get-db-js-command (str "localStorage.getItem(" local-storage-db-key ");"))

(defn get-todos-db []
  (js/mainWindow.webContents.executeJavascript "console.log('Hey!')" true))

(defn show-notification []
  (Notification. (clj->js {:title "Basic Notification" :body "Notification from main process"})))

(defn init-browser []
  (create-window)
  (.on app "activate" #(when-not (.-length (.getAllWindows BrowserWindow))
                         (create-window))))

(defn main []
  (.on app "window-all-closed" #(when-not (= js/process.platform "darwin")
                                  (.quit app)))
  (.then (.whenReady app)  init-browser))
