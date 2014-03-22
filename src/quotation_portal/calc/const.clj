(ns quotation-portal.calc.const)

(def alpha 0.003)
(def beta 0.02)
(def gamma 0.002)

(defn get-column-key [gender]
  (case gender
    "male" :B
    "female" :C
    nil))