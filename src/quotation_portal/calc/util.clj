(ns quotation-portal.calc.util
  (:require [clj-time.core :as t]
            [clj-time.local :as l]
            [clj-time.format :as f]))

(defn get-date [string-date]
  (f/parse (f/formatter "dd-MM-yyyy") string-date))

(defn get-years [date-b date-a]
  "based on the given string values of date-b and date-a retuns the number of years in between"
  (println date-b)
  (println date-a)
  (t/in-years (t/interval (get-date date-b) (get-date date-a))))

(defn get-age [dob]
  "based on the given string DoB returns the age"
  (t/in-years (t/interval (get-date dob) (l/local-now))))