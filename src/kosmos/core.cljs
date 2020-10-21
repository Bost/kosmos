(ns kosmos.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch reg-event-db]]
            [kosmos.file-storage.fx]
            [kosmos.file-storage.db :as fs]
            [kosmos.window.core]
            [kosmos.clojure.core]))

(reg-event-db :init #(merge % fs/db))

(def placeholder-id "app")

(defn ^:dev/after-load start []
  (->> placeholder-id
       (.getElementById js/document)
       (rdom/render [:div "application"])))

(defn init! []
  (dispatch [:init])
  (start))
