(ns kosmos.canvas.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-frame.core :refer [subscribe]]
            [kosmos.canvas.paper :as paper]))

(def current-project (atom nil))

(defn canvas-container [ast child]
  (let [project (atom nil)]
    (r/create-class
     {:reagent-render child
      :component-did-mount
      (fn [comp]
        (reset! project (-> comp rdom/dom-node paper/make-project))
        (paper/make-point-text {:point [20 20]
                                :content (pr-str (:form ast))
                                :fillColor "black"
                                :fontSize 15
                                :data {:kid 1}}))
      :component-did-update
      (fn [this]
        (let [[ast _child] (r/props this)
              [text-item] (-> @current-project
                              (paper/get-items {:data {:kid 1}}))]
          (set! (.-content text-item) (pr-str (:form ast)))))})))

(defn canvas []
  (let [ast (subscribe [:fs/current-file-ast])]
    (fn []
      [canvas-container @ast
       [:canvas {:width 500 :height 500}]])))

(comment
  (let [text-item (-> @current-project
                      (paper/get-items {:data {:kid 1}})
                      first)]
    (set! (.-content text-item) "Found")))
