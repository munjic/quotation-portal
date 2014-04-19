(ns quotation-portal.routes.policymgmt
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all])
  (:use [quotation-portal.routes.params :as params :refer :all]
        [quotation-portal.routes.util :as util]))

(defn policymgmt []
  (layout/common 
    "QP - Configuration"
    (list 
      [:h2 "Policy Management"]
      [:i "Below is the list of policies which can be approved, cancelled or deleted."]

               )))

(defroutes polmgmt-routes
  (GET "/policy-management" [] (policymgmt)))