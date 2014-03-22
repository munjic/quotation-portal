(ns quotation-portal.routes.config
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all])
  (:use [quotation-portal.routes.constants :as const :refer :all]))

(defn control [label body]
  [:div {:class "row"}
   [:span {:class "label"} label]
   body])

(defn config []
  (layout/common 
    "QP - Configuration"
    (list 
      [:h2 "Config page"]
      (form-to [:post "/"]
               (control "Interest rate" (drop-down :interest-rate const/interest-rates))
               (control "alpha" (text-field "alpha"))
               (control "beta" (text-field "beta"))
               (control "gamma" (text-field "gamma"))
               ))))

(defroutes config-routes
  (GET "/config" [] (config)))

