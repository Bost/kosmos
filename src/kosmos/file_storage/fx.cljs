(ns kosmos.file-storage.fx
  (:require [re-frame.core :refer [reg-fx dispatch]]))

(def fs (js/require "fs"))

(defn read-file [{:keys [path on-success on-failure]}]
  (try
    (->> (.readFileSync fs path)
         (conj on-success)
         (dispatch))
    (catch js/Object e
      (dispatch (conj on-failure e)))))

(reg-fx :fs/read-file read-file)
