(ns quotation-portal.routes.policymgmt
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all]
            [quotation-portal.model.db :as db]
            [ring.util.response :only redirect])
  (:use [quotation-portal.routes.params :as params :refer :all]
        [quotation-portal.routes.util :as util]))

;;outcome status of actions
(def action-status
  {:submit "Submitted"
   :approve "In-force"
   :cancel "Cancelled"
   :reinstate "In-force"})

;;applicable actions for status
(def status-action
  { :submitted "Approve"
    :in-force "Cancel"
    :cancelled "Reinstate"})

(defn get-action [status]
  (val (find status-action (keyword (clojure.string/lower-case status)))))

(defn get-outcome-status [action]
  (val (find action-status (keyword action))))

(defn make-action-button [action id]
  (form-to [:post (str "policy-management/" (clojure.string/lower-case action) "/" id)]
           (submit-button action)))

(defn make-delete-button [id]
  (form-to [:delete (str "policy-management/" id)]
           (submit-button "Delete")))

(defn show-policies []
  [:table.policies
   [:tr
    [:th "Policy Holder"]
    [:th "Benefit Amount"]
    [:th "Benefit Type"]
    [:th "Premium Amount"]
    [:th "Insurance period"]
    [:th "Payment period"]
    [:th "Payment frequency"]
    [:th "Status"]
    [:td ""]
    [:td ]]
   (for [{:keys [id policy_holder benefit_type benefit_amount premium_amount contract_start contract_end payment_start payment_end pay_freq status]} (db/read-policies)]
     [:tr
      [:td policy_holder]
      [:td benefit_amount]
      [:td benefit_type]
      [:td premium_amount]
      [:td (str contract_start " - " contract_end)]
      [:td (str payment_start " - " payment_end)]
      [:td (util/get-pay-freq pay_freq)]
      [:td status]
      [:td (make-action-button (get-action status) id)]
      [:td (make-delete-button id)]])])
      

(defn policymgmt []
  (layout/common 
    "QP - Policy Management"
    (list 
      [:h2 "Policy Management"]
      [:i "Below is the list of policies submitted through the Application Form. The policies can be approved, cancelled or deleted."][:br][:br]
      (show-policies))))

(defn do-action [action id]
  (db/set-status (get-outcome-status action) id)
  (ring.util.response/redirect "/policy-management"))

(defn delete-policy [id]
  (db/delete-policy id)
  (ring.util.response/redirect "/policy-management"))

(defroutes polmgmt-routes 
  (GET "/policy-management" [] (policymgmt))
  (DELETE "/policy-management/:id" [id] (delete-policy id))
  (POST "/policy-management/:action/:id" [action id] (do-action action id)))