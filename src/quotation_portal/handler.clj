(ns quotation-portal.handler
  (:require [compojure.core :refer [defroutes routes]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [hiccup.middleware :refer [wrap-base-url]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [quotation-portal.routes.home :refer [home-routes]]
            [quotation-portal.routes.config :refer [config-routes]]
            [quotation-portal.routes.policymgmt :refer [polmgmt-routes]]))

(defn init []
  (println "quotation-portal is starting"))

(defn destroy []
  (println "quotation-portal is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (routes polmgmt-routes config-routes home-routes app-routes)
      (handler/site)
      (wrap-base-url)))


