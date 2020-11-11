(ns kosmos.canvas.paper
  (:require [paper :as paper]))

;;;;;;;;;;;;;;;;;
;; PaperScoope

(defn setup [dom-element]
  (paper/setup dom-element))

(defn project []
  (.-view paper))

;;;;;;;;;;;;;;;;;
;; Path

(defn make-path [attrs]
  (paper/Path. (clj->js attrs)))

(defn make-point-text [attrs]
  (paper/PointText. (clj->js attrs)))

;;;;;;;;;;;;;;;;;
;; Project

(defn make-project [element]
  (paper/Project. element))

(defn get-items [^paper/Project project opts]
  (.getItems project (clj->js opts)))
