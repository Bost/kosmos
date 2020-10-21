(ns kosmos.file-storage.events
  (:require [re-frame.core :refer [reg-event-db]]))

(defn set-current-file [db [_ path content ast]]
  (-> db
      (assoc-in [:fs :path] path)
      (assoc-in [:fs :content] content)
      (assoc-in [:fs :ast] ast)))

(reg-event-db :fs/set-current-file set-current-file)
