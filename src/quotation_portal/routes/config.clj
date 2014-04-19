(ns quotation-portal.routes.config
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all])
  (:use [quotation-portal.routes.params :as params :refer :all]
        [quotation-portal.routes.util :as util]))

(defn configure [interest-rate alpha beta gamma]
  (params/reset params/interest-rate interest-rate)
  (params/reset params/alpha alpha)
  (params/reset params/beta beta)
  (params/reset params/gamma gamma)
  (config))

(defn config []
  (layout/common 
    "QP - Configuration"
    (list 
      [:h2 "Product Configuration"]
      [:i "The settings are applied for all supported insurance products"]
      [:br][:br]
      (form-to [:post "/configure"]
               (util/control "Interest rate" (text-field "interest-rate") (str "Current value: " (params/get-interest-rate)))
               (control "alpha" (text-field "alpha") (str "Current value: " (params/get-alpha)))
               (control "beta" (text-field "beta") (str "Current value: " (params/get-beta)))
               (control "gamma" (text-field "gamma") (str "Current value: " (params/get-gamma)))
               (hiccup.form/submit-button "Change values")))))

(defroutes config-routes
  (GET "/config" [] (config))
  (POST "/configure" [interest-rate alpha beta gamma] (configure interest-rate alpha beta gamma)))

