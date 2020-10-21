(ns kosmos.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch reg-event-db subscribe]]
            [kosmos.file-storage.db :as fs]
            [kosmos.file-storage.fx]
            [kosmos.file-storage.events]
            [kosmos.file-storage.subs]
            [kosmos.window.core]
            [kosmos.clojure.core]))

(reg-event-db :init #(merge % fs/db))

(def placeholder-id "app")

(defn app []
  (let [content (subscribe [:fs/current-file-content])]
    [:div @content]))

(defn ^:dev/after-load start []
  (->> placeholder-id
       (.getElementById js/document)
       (rdom/render [app])))

(defn init! []
  (dispatch [:init])
  (start))
