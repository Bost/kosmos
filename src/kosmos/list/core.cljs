(ns kosmos.list.core
  (:require [garden.core :refer [css]]))

(def db
  {:nodes [{:value "def" :x 50 :y 50 :edges [{:from {:x 50 :y 50} :to {:x 80 :y 75}}
                                             {:from {:x 50 :y 50} :to {:x 80 :y 100}}]}
           {:x 80 :y 75 :value "greeting" :edges []}
           {:x 80 :y 100 :value "\"Hello\"" :color "#3B9A2B" :edges []}
           {:x 50 :y 150 :value "print" :edges [{:from {:x 50 :y 150} :to {:x 80 :y 175}}]}
           {:x 80 :y 175 :value "greeting" :edges []}]})

;; Components

(defn dot [cx cy]
  [:circle.dot {:cx cx :cy cy}])

(defn edge [{:keys [from to]}]
  [:g
   [:line {:x1 (:x from) :y1 (:y from) :x2 (:x from) :y2 (:y to) :stroke "#e4e4e4" :stroke-width 1.5}]
   [:line {:x1 (:x from) :y1 (:y to) :x2 (:x to) :y2 (:y to) :stroke "#e4e4e4" :stroke-width 1.5}]])

(defn node [{:keys [x y value color]}]
  (let [cx x cy y tx (+ x 12) ty (+ y 4)]
    [:g
     [dot cx cy]
     [:foreignObject {:x tx :y (- ty 12) :width 100 :height 20}
      [:input {:style {:font-family "Fira Code" :font-size "14px" :color color :border 0 :outline "none"} 
               :value value
               :on-change (fn [e] (print e))}]]]))

(defn node-with-edges [data]
  [:g
   (map #(-> [edge %]) (:edges data))
   [node data]])

(def styles
  [:.dot {:fill "#fff"
          :stroke "#444"
          :stroke-width 2
          :r 4}
   [:&:hover {:r 6}]])

(defn app []
  (let [nodes (:nodes db)]
    [:div
     [:style (css styles)]
     [:svg {:height 500 :width 500}
      (map #(-> [node-with-edges %]) nodes)]]))
