(ns kosmos.flag)

(def db {:svg false
         :svg-list false})

(defn enabled? [name] (get db name))
