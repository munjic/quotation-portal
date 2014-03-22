(ns quotation-portal.routes.home
  (:require [compojure.core :refer :all]
            [quotation-portal.views.layout :as layout]
            [hiccup.form :refer :all]
            [clj-time.core :as t])
  (:use [quotation-portal.calc.calc :as calc :refer [gross-premium]]
        [quotation-portal.calc.util :as util]))

(defn control [label body]
  [:div {:class "row"}
   [:span {:class "label"} label]
   body])


(defn home [& [name premium]]
  (layout/common "QP - Home"
   (list (if name [:p (str "Premium " premium " for: " name)])
         (form-to [:post "/"]
                  [:h2 "Personal information"]
                  (control "Full name" (text-field "name"))
                  (control "Email" (email-field "email"))
                  (control "Date of Birth" (text-field "dob"))
                  (control "Gender" (text-field "gender"))
                  [:h2 "Contract data"]
                  (control "Start of insurance" (text-field "contract-start"))
                  (control "End of insurance" (text-field "contract-end"))
                  (control "Benefit type" 
                          (drop-down :insurance [:Death :Survival :Endownment] :Survival))
                  (control "Insured sum"(text-field "ins-sum"))
                  [:h3 "Payment information"]
                  (control "Payment start date" (text-field "pay-start"))
                  (control "Payment end date" (text-field "pay-end"))
                  (control "Payment frequency" (text-field "pay-freq"))
                  (submit-button "Submit")))))

(defn calc-prem [name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq]
    (home name (calc/gross-premium
                 "death"
                 (util/get-age dob)
                 (util/get-years contract-start contract-end)
                 (util/get-years pay-start pay-end)
                 (read-string pay-freq)
                 (read-string ins-sum)
                 0.003
                 0.02
                 0.002)))
(defroutes home-routes
  (GET "/" [] (home))
  (POST "/" [name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq] 
        (calc-prem name email dob gender contract-start contract-end insurance ins-sum pay-start pay-end pay-freq)))
