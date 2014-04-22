(ns quotation-portal.routes.config
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all]
            [ring.util.response :only redirect])
  (:use [quotation-portal.routes.params :as params :refer :all]
        [quotation-portal.routes.util :as util]))

(defn config []
  (layout/common 
    "QP - Configuration"
    (list 
      [:h2 "Product Configuration"]
      [:i "The settings are applied for all supported insurance products"]
      [:br][:br]
      (form-to [:post "/configure"]
               [:h4 "Interest rate"]
               (util/control "Interest rate" (text-field "interest-rate") (str "Current value: " (params/get-interest-rate)))
               [:h4 "Insurance costs"]
               (control "alpha" (text-field "alpha") (str "Current value: " (params/get-alpha)))
               (control "beta" (text-field "beta") (str "Current value: " (params/get-beta)))
               (control "gamma" (text-field "gamma") (str "Current value: " (params/get-gamma)))
               [:div {:class "button"} (submit-button "Change values")]))))

(defn configure [interest-rate alpha beta gamma]
  (params/reset params/interest-rate interest-rate)
  (params/reset params/alpha alpha)
  (params/reset params/beta beta)
  (params/reset params/gamma gamma)
  (ring.util.response/redirect "config"))

(defroutes config-routes
  (GET "/config" [] (config))
  (POST "/configure" [interest-rate alpha beta gamma] (configure interest-rate alpha beta gamma)))

