(ns kosmos.window.fx
  (:require [re-frame.core :refer [reg-fx dispatch]]
            [electron :refer [ipcRenderer]]))

(defn set-title [title]
  (set! (.-title js/document) title))

(defn show-open-dialog [{:keys [on-success]}]
  (let [handle-results
        (fn [result]
          (let [result (js->clj result :keywordize-keys true)
                {canceled? :canceled paths :filePaths} result
                path (first paths)]
            (when-not canceled?
              (dispatch (conj on-success path)))))]
    (-> (.invoke ipcRenderer "show-open-dialog")
        (.then handle-results))))

(reg-fx :window/title set-title)

(reg-fx :window/show-open-dialog show-open-dialog)
