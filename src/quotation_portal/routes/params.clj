(ns quotation-portal.routes.params)

(def interest-rate (atom 0.05))

(def alpha (atom 0.03))

(def beta (atom 0.02))

(def gamma (atom 0.002))

(def interest-rates [0.02 0.03 0.04 0.05 0.06])

(defn get-alpha [] @alpha)
(defn get-beta [] @beta)
(defn get-gamma [] @gamma)
(defn get-interest-rate [] @interest-rate)

(defn reset [atom value]
  (if (and (not (nil? value)) (not= value ""))
    (reset! atom (read-string value))))