(ns quotation-portal.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]))

(defn make-menu [& items]
  [:div.menu [:ul (for [item items] [:li item])]])

(defn menu []
  (make-menu
    (link-to "/" "Policy submission")
    (link-to "/policy-management" "Policy Management")
    (link-to "/config" "Configuration")
    (link-to "/" "About")))

(defn common [& [title content]]
  (html5
    [:head
     [:title title]
     (include-css "/css/screen.css")]
    [:body 
     [:h1 "Quotation Portal"]
     [:hr]
     (menu)
     [:hr]
     [:div.content 
     content]]))

