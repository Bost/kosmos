(ns kosmos.window.core
  (:require [re-frame.core :refer [dispatch]]
            ["mousetrap" :as mousetrap]
            [kosmos.window.fx]
            [kosmos.window.events]))

(defn reg-shortcut [keys handler]
  (.bind mousetrap keys handler))

(reg-shortcut "command+o" (fn []
                            (dispatch [:open-file])))
