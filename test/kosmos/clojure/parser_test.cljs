(ns kosmos.clojure.parser-test
  (:require [clojure.test :refer [deftest is]]
            [kosmos.clojure.parser :as p]))

(deftest parse-empty-test
  (is (= [] (p/parse "")))
  (is (= [{:form [{:form '+ :type :symbol :meta nil}
                  {:form 2 :type :number :meta nil}
                  {:form 3 :type :number :meta nil}]
           :type :list
           :meta nil}]
         (p/parse (pr-str '(+ 2 3))))))

(deftest transform-test
  (is (= {"1" {:form '+ :type :symbol :id "1" :children ["2" "3"]}
          "2" {:form 2 :type :number :id "1" :parent "1"}
          "3" {:form 3 :type :number :id "2" :parent "1"}}
         (-> '(+ 2 3) pr-str p/parse p/transform))))
