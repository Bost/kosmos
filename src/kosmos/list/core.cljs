(ns kosmos.list.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :refer [dispatch reg-event-db reg-sub subscribe]]))

(def nodes 
  {:type :file 
   :name "untitled" 
   :children []})

;; Event handlers

(reg-event-db
 :init
 (fn [_ _]
   {:cursor {:visible true :x 87 :y 40}}))

(reg-event-db 
 :cursor/tick
 (fn [db [_ _]]
   (let [current (get-in db [:cursor :visible])]
     (assoc-in db [:cursor :visible] (not current)))))

(reg-event-db
 :cursor/move-left
 (fn [db [_ _]]
   (let [x (get-in db [:cursor :x])
         step 8]
     (-> db
         (assoc-in [:cursor :x] (- x step))
         (assoc-in [:cursor :visible] true)))))

(reg-event-db
 :cursor/move-right
 (fn [db [_ _]]
   (let [x (get-in db [:cursor :x])
         step 8]
     (-> db
         (assoc-in [:cursor :x] (+ x step))
         (assoc-in [:cursor :visible] true)))))

;; Subscriptions

(reg-sub 
 :cursor
 (fn [db _]
   (:cursor db)))

(reg-sub
 :cursor-visible
 (fn [_ _]
   (subscribe [:cursor]))
 (fn [cursor _]
   (:visible cursor)))

(reg-sub
 :cursor-x
 (fn [_ _]
   (subscribe [:cursor]))
 (fn [cursor _]
   (:x cursor)))

(reg-sub
 :cursor-y
 (fn [_ _]
   (subscribe [:cursor]))
 (fn [cursor _]
   (:y cursor)))

;; Components

(defn edge [{:keys [from to]}]
  [:g
   [:line {:x1 (:x from) :y1 (:y from) :x2 (:x from) :y2 (:y to) :stroke "#e4e4e4" :stroke-width 1.5}]
   [:line {:x1 (:x from) :y1 (:y to) :x2 (:x to) :y2 (:y to) :stroke "#e4e4e4" :stroke-width 1.5}]])

(defn node [{:keys [x y value color]}]
  (let [cx x cy y tx (+ x 12) ty (+ y 4)]
    [:g
     [:circle {:cx cx :cy cy :r 4 :fill "#fff" :stroke "#444" :stroke-width 2}]
     [:foreignObject {:x tx :y (- ty 12) :width 100 :height 20}
      [:input {:style {:font-family "Fira Code" :font-size "14px" :color color :border 0 :outline "none"} 
               :value value
               :on-change (fn [e] (print e))}]]]))

(defn cursor []
  (let [cursor-visible? (subscribe [:cursor-visible])
        opacity (if @cursor-visible? 0 1)
        x (subscribe [:cursor-x])
        y (subscribe [:cursor-y])]
    [:line {:x1 @x :y1 @y :x2 @x :y2 (+ @y 18) :stroke "#3563C9" :stroke-width 2 :opacity opacity}]))

(defn app []
  [:svg
   [edge {:from {:x 50 :y 50} :to {:x 80 :y 75}}]
   [edge {:from {:x 50 :y 50} :to {:x 80 :y 100}}]
   [node {:x 50 :y 50 :value "def"}]
   [node {:x 80 :y 75 :value "greeting"}]
   [node {:x 80 :y 100 :value "\"Hello\"" :color "#3B9A2B"}]])

;; Misc

(defonce cursor-timer
  (let [duration 530]
    (js/setInterval #(dispatch [:cursor/tick]) duration)))

(defn app-element []
  (.getElementById js/document "app"))

(defn ^:dev/after-load mount []
  (rdom/render [app] (app-element)))

(defn handle-keydown [e]
  (print (.-keyCode e))
  (let [key-code (.-keyCode e)]
    (cond 
      (= key-code 37) (dispatch [:cursor/move-left])
      (= key-code 39) (dispatch [:cursor/move-right]))))

(defn catch-keypress []
  (.addEventListener js/document "keydown" handle-keydown))

(defn start! []
  (dispatch [:init])
  (print "Started")
  (mount)
  (comment (catch-keypress)))
