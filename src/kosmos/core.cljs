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

(defn ast-node [{:keys [op children form] :as data}]
  (if children
    [:div.node
     [:div.value op]
     [:div.children {:style {:margin-left 20}}
      (map (fn [key]
             (let [child (get data key)]
               (if (vector? child)
                 (map #(-> ^{:key (random-uuid)} [ast-node %]) child)
                 [ast-node child])))
           children)]]
    [:div.node
     [:div.value (pr-str form)]]))

(defn app []
  (let [ast @(subscribe [:fs/current-file-ast])]
    (when ast [ast-node ast])))

(defn ^:dev/after-load start []
  (->> placeholder-id
       (.getElementById js/document)
       (rdom/render [app])))

(defn init! []
  (dispatch [:init])
  (start))
