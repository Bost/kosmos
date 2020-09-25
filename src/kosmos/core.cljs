(ns kosmos.core
  (:require [kosmos.config-file :as cf]
            [reagent.dom :as dom]
            [kosmos.component :refer [container node edge]]
            [kosmos.flag :refer [enabled?]]
            [kosmos.fx]
            [kosmos.events]
            [re-frame.core :as rf]
            [kosmos.io.clojure]
            [kosmos.list.core :as kl]))

(defn load [filename]
  (clj->js (cf/load filename)))

(defn dispatch [event]
  (let [[name params] (js->clj event)
        name (keyword name)]
    (rf/dispatch [name params])))

(defn parse [data]
  (-> data kosmos.io.clojure/parse clj->js))

;;;;

(defn render-svg-canvas []
  (let [el (.getElementById js/document "app")]
    (dom/render
     [container 1000 1000 [[edge 20 64 140 64]
                           [node 20 50 "println" false]
                           [node 140 50 "42" true]]]
     el)))

(defn render-list-canvas []
  (let [el (.getElementById js/document "app")]
    (dom/render [kl/app] el)))

(defn ^:dev/after-load start []
  (when (enabled? :svg) (render-svg-canvas))
  (when (enabled? :svg-list) (render-list-canvas)))

(defn start! [] 
  (rf/dispatch [:init])
  (start))

(comment (rf/dispatch [:file/open "bla/one.txt"]))
