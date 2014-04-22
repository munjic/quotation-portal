(ns quotation-portal.routes.about
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all]
            [ring.util.response :only redirect])
  (:use [quotation-portal.routes.params :as params :refer :all]
        [quotation-portal.routes.util :as util]))

(defn about []
  (layout/common 
    "QP - About"
    (list
      [:h2 "About"]
      [:h3 "About Quotation Portal"]
      [:p "This is a mock-up quotation and policy management portal for life insurance products. The portal provides premium calculation and managing of policies for the supported products."]
      [:h3 "About Insurance Products"]
      [:p "The following benefit types are available:"]
      [:h4 "Death benefit"]
      [:p "The benefit amount is payable to a beneficiary of a deceased."]
      [:h4 "Survival benefit"]
      [:p "The benefit amount is payable to a beneficiary when the insured person lives after the contract expiration."]
      [:h4 "Endownment benefit"]
      [:p "Endownment benefit covers both Death and Survival risks: "]
      [:ul 
       [:li "The benefit amount is payable to a beneficiary if the insured person dies while contract is in-force,"]
       [:li "The benefit amount is payable to a beneficiary if the insured person lives after contract expiration."]]
      
      )))

(defroutes about-routes 
  (GET "/about" [] (about)))