(ns kosmos.window.events
  (:require [cljs.pprint :as p]
            [re-frame.core :refer [reg-event-fx]]))

(def js-path (js/require "path"))

(defn filename [path]
  (->> path (.parse js-path) .-base))

(defn open-file [_ _]
  {:window/show-open-dialog {:on-success [:open-file/selected]}})

(defn open-file-selected [_ [_ path]]
  {:fs/read-file {:path path
                  :on-success [:open-file/read path]
                  :on-failure [:open-file/failed]}})

(defn open-file-read [_ [_ path content]]
  {:window/title (filename path)
   :clojure/unpack {:content (.toString content)
                    :on-success [:open-file/unpacked]
                    :on-failure [:open-file/failed]}})

(defn open-file-unpacked [_ [_ ast]]
  (p/pprint ast))

; TODO: handle failure case
(defn open-file-failed [_ [_ _]]
  (print "failed"))

(reg-event-fx :open-file open-file)
(reg-event-fx :open-file/selected open-file-selected)
(reg-event-fx :open-file/read open-file-read)
(reg-event-fx :open-file/unpacked open-file-unpacked)
(reg-event-fx :open-file/failed open-file-failed)
