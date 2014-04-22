(ns quotation-portal.routes.home
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all]
            [clj-time.core :as t]
            [quotation-portal.model.db :as db])
  (:use [quotation-portal.calc.calc :as calc]
        [quotation-portal.calc.util :as calc-util]
        [quotation-portal.routes.util :as util]))
(defn home [& [message]]
  (layout/common "QP - Home"
   (list [:p message]
         [:h2 "Application form"]
         [:i "After successfully submitted, application is added to the policy approval list in the Policy Management."]
         (form-to [:post "/"]
                  [:h3 "Personal information"]
                  (util/control "Full name*" (text-field "name") "Enter your full name")
                  (util/control "Email*" (email-field "email") )
                  (util/control "Date of Birth*" (text-field "dob") "Enter date in format: dd-MM-yyyy")
                  (util/control "Gender" (drop-down :gender [:Male :Female] :Male))
                  [:h3 "Contract data"]
                  (util/control "Start of insurance*" (text-field "contract-start"))
                  (util/control "End of insurance*" (text-field "contract-end"))
                  (util/control "Benefit type" 
                          (drop-down :insurance [:Death :Survival :Endownment] :Survival))
                  (util/control "Benefit amount*"(text-field "ins-sum"))
                  [:h4 "Payment information"]
                  (util/control "Payment start date*" (text-field "pay-start"))
                  (util/control "Payment end date*" (text-field "pay-end"))
                  (util/control "Payment frequency*" (drop-down :pay-freq (util/pay-freq)))
                  [:div {:class "button"} (submit-button "Submit")]
                  [:div {:class "reset-btn"} (reset-button "Reset form")]))))

(defn calc-prem [name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq]
  (let [premium 
        (read-string  
          (format 
            "%.2f"
            (calc/gross-premium-interface
              (clojure.string/lower-case insurance)
								   (calc-util/get-age dob)
								   (calc-util/get-years contract-start contract-end)
								   (calc-util/get-years pay-start pay-end)
								   (read-string pay-freq)
								   (read-string ins-sum)
              (clojure.string/lower-case gender))))]
    (db/save-policy name insurance premium ins-sum contract-start contract-end pay-start pay-end pay-freq)
    (home (str "Policy successfully submitted for " name ". Premium " premium " for " insurance " benefit."))))
(defroutes home-routes
  (GET "/" [] (home))
  (POST "/" [name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq] 
        (calc-prem name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq)))
