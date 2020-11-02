(ns kosmos.electron
  (:require [electron :refer [app BrowserWindow ipcMain dialog]]))

(def window (atom nil))

(def index-url (str "file://" js/__dirname "/index.html"))

(def darwin? (= js/process.platform "darwin"))

(.handle ipcMain "show-open-dialog" #(.showOpenDialog dialog))

(defn create-window []
  (let [options (clj->js {:width 800 :height 600 :webPreferences {:nodeIntegration true}})]
    (reset! window (BrowserWindow. options)))
  (.loadURL ^js/BrowserWindow @window index-url)
  (.on ^js/BrowserWindow @window "close" #(reset! window nil)))

(defn main []
  (.on app "window-all-closed" #(when-not darwin?
                                  (.quit app)))
  (.on app "ready" create-window))
