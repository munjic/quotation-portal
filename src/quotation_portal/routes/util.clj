(ns quotation-portal.routes.util)

(defn control [label body & [help-text]]
  [:div {:class "row"}
   [:span {:class "label"} label]
   body
   [:span {:class "help-text"} help-text]])

(defn pay-freq [] 
  [["Annualy" "1"]
   ["Semi-Annually" "2"]
   ["Quarterly" "3"]
   ["Monthly" "12"]])

(defn get-pay-freq [id]
  (first (for [x (pay-freq)
               :when (= (nth x 1) (str id))]
           (nth x 0))))
  