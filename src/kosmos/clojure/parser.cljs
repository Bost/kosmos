(ns kosmos.clojure.parser
  (:require [cljs.js :as cljs]))

 (defn wrap [source]
   (str "[" source "]"))

 (defn unwrap [ast]
   (if (:value ast)
     (assoc-in ast [:value :op] :file)
     ast))

 (defn elide-env [_env ast _opts]
   (dissoc ast :env))

(defn parse [source cb]
  (let [state (cljs/empty-state)
        name "temp"
        opts {:passes [elide-env]
              :load #(%2 {:lang :clj :source ""})
              :analyze-deps false}]
    (cljs/analyze-str state (wrap source) name opts #(-> % unwrap cb))))
