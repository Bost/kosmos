(ns kosmos.core
  (:require [reagent.dom :as dom]
            [kosmos.fx]
            [kosmos.events]
            [kosmos.subs]
            [kosmos.components :refer [canvas]]
            [kosmos.flag :refer [enabled?]]
            [kosmos.api]
            [re-frame.core :as rf]))

(defn render-svg-canvas []
  (let [el (.getElementById js/document "app")]
    (dom/render [canvas] el)))

(defn ^:dev/after-load start []
  (when (enabled? :svg) (render-svg-canvas)))

(defn start! [] 
  (rf/dispatch [:init])
  (start))
