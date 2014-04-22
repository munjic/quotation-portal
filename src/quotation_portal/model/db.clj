(ns quotation-portal.model.db
  (:require [clojure.java.jdbc :as sql])
  (:import java.sql.DriverManager))

(def db {:classname "org.sqlite.JDBC",
         :subprotocol "sqlite"
         :subname "policies.sq3"})

(defn create-policy-table []
  (sql/with-connection
    db
    (sql/create-table
      :policy
      [:id "INTEGER PRIMARY KEY AUTOINCREMENT"]
      [:timestamp "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"]
      [:policy_holder "TEXT"]
      [:benefit_type "TEXT"] ;death/survival/endownment
      [:premium_amount "DOUBLE"]
      [:benefit_amount "DOUBLE"]
      [:contract_start "TEXT"]
      [:contract_end "TEXT"]
      [:payment_start "TEXT"]
      [:payment_end "TEXT"]
      [:pay_freq "INTEGER"]
      [:status "TEXT"]))) ;submitted/approved/cancelled

(defn read-policies []
  (sql/with-connection
    db
    (sql/with-query-results res
      ["SELECT * FROM policy ORDER BY timestamp DESC"]
      (doall res))))

(defn save-policy [policy_holder benefit_type premium_amount benefit_amount contract_start contract_end payment_start payment_end pay_freq]
  (sql/with-connection
    db
    (sql/insert-values
      :policy
      [:policy_holder :benefit_type :premium_amount :benefit_amount :contract_start :contract_end :payment_start :payment_end :pay_freq :status]
      [policy_holder benefit_type premium_amount benefit_amount contract_start contract_end payment_start payment_end pay_freq "Submitted"])))

(defn set-status [status id]
  (sql/with-connection
    db
    (sql/update-values 
      :policy 
      ["id=?" id] 
      {:status status})))

(defn delete-policy [id]
  (sql/with-connection
    db
    (sql/delete-rows
      :policy
      ["id=?" id])))