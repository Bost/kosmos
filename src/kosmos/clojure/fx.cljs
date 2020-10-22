(ns kosmos.clojure.fx
  (:require [re-frame.core :refer [reg-fx dispatch]]
            [kosmos.clojure.parser :refer [parse]]))

(defn unpack [{:keys [content on-success on-failure]}]
  (let [handler (fn [result]
                  (if (:value result)
                    (dispatch (conj on-success (:value result)))
                    (dispatch (conj on-failure result))))]
    (parse content handler)))

(reg-fx :clojure/unpack unpack)
