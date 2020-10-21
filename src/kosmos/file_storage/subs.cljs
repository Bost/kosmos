(ns kosmos.file-storage.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :fs/current-file
 (fn [db _]
   (:fs db)))

(reg-sub
 :fs/current-file-content
 :<-[:fs/current-file]
 (fn [{:keys [content]} _]
   content))
