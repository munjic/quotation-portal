(ns quotation-portal.routes.util)

(defn control [label body & [help-text]]
  [:div {:class "row"}
   [:span {:class "label"} label]
   body
   [:span {:class "help-text"} help-text]])