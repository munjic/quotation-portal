(ns quotation-portal.calc.calc
  (:use [dk.ative.docjure.spreadsheet]
        [quotation-portal.routes.params :as params :refer :all]) 
  (:require [clojure.math.numeric-tower :as math]))


(defn get-column-key [gender]
  (case gender
    "male" :B
    "female" :C
    nil))

(defn read-qx [workbook sheet gender]
  "reads from the mortality sheet cutting of the header row value"
  (subvec (->> (load-workbook workbook)
            (select-sheet sheet)
            (select-columns {
                             ;:A :x, 
                             (get-column-key gender) :q-x})) 1))

(defn calc-v [i]
  "given the interest rate i calculates v"
 (/ 1 (+ 1 i)))

(defn get-qx [qx-vec-of-maps]
  "Given the vector of qx value maps returns the vector of clean qx values"
  (loop [qx-out [] 
         qx-in qx-vec-of-maps]
    (if (empty? qx-in)
      qx-out
      (recur (conj qx-out (:q-x (first qx-in))) (rest qx-in)))))

(defn calc-px [qx]
  "Given the vector of qx values calculates the vector of px values"
  (loop [px-out [] 
         qx-in qx]
    (if (empty? qx-in)
      px-out
      (recur (conj px-out (- 1 (first qx-in))) (rest qx-in)))))
      
(defn calc-lx [px l0]
  "Given the vector of px values and the initial l0 value calculates the lx values"
  (loop [lx-out [l0] 
         px-in px]
    (if (empty? (rest px-in))
      (reverse lx-out)
      (recur (conj (seq lx-out) (* (first lx-out) (first px-in))) (rest px-in)))))
  
(defn calc-Dx [lx]
  "Given the vector of lx values returns the dx values"
  (loop [dx-out [] 
         lx-in lx]
    (if (empty? (rest lx-in))
      dx-out
      (recur (conj dx-out (- (first lx-in) (first (rest lx-in)))) (rest lx-in)))))

(defn calc-Dx [lx v]
 "Given the vector of lx values and the v value returns Dx values"
 (loop [Dx-out [] 
        lx-in lx 
        x 0]
  (if (empty? lx-in)
    Dx-out
    (recur (conj Dx-out (* (first lx-in) (math/expt v x))) (rest lx-in) (inc x)))))

(defn calc-Cx [dx v]
  "Given the vector of dx values and the v value returns Cx values"
  (loop [Cx-out [] 
         dx-in dx 
         x 0]
    (if (empty? dx-in)
      Cx-out
      (recur (conj Cx-out (* (first dx-in) (math/expt v (+ x 1)))) (rest dx-in) (inc x)))))

(defn sum [vec]
  "Given the vector returns the sum of vector elements"
  (loop [sum 0 
         vec-in vec]
    (if (empty? vec-in)
      sum
      (recur (+ sum (first vec-in)) (rest vec-in)))))

(defn calc-Nx [Dx]
  "Given the vector of Dx values returns Nx values"
  (loop [Nx-out [] 
         Dx-in Dx]
    (if (empty? Dx-in)
      Nx-out
      (recur (conj Nx-out (sum Dx-in)) (rest Dx-in)))))

(defn calc-Mx [Cx]
  "Given the vector of Cx values returns Mx values"
  (loop [Mx-out [] 
         Cx-in (reverse (rest (reverse Cx)))]
    (if (empty? Cx-in)
      Mx-out
      (recur (conj Mx-out (sum Cx-in)) (rest Cx-in)))))

(defn calc-Mx2 [qx]
  "Calculates Mx value based on the given qx vector"
  (calc-Mx 
    (calc-Cx 
      (calc-Dx 
        (calc-lx 
          (calc-px qx)
          100000)) 
      (calc-v (params/get-interest-rate)))))

(defn calc-Dx2 [qx]
  "Calculates Dx value based on the given qx vector"
  (calc-Dx 
    (calc-lx 
      (calc-px qx) 100000) (calc-v (params/get-interest-rate))))

(defn nEx [Dx x n]
  "Given the age of insured person x and the contract duration n returns nEx value"
  (/ (nth Dx (+ x n)) (nth Dx x)))

(defn nAx [Mx Dx x n]
  "Given the age of insured person x and the contract duration n returns nAx value"
  (/ (- (nth Mx x) (nth Mx (+ x n))) (nth Dx x)))

(defn net-single-premium [nEx nAx insurance sum-insured]
  "Given the nEx value, nAx value, insurance type and sum insured calculates net single premium"
  (* sum-insured
     (case insurance
       "saving" nEx 
       "death" nAx
       "endownment" (+ nEx nAx) 0)))

(defn annuity-factor [Nx Dx x t]
  "Given the age of insured person x and premium payment duration returns annuity factor"
  (/ (- 
       (nth Nx x) (nth Nx (+ x t))) 
     (nth Dx x)))

(defn adj-annuity-factor [annuity-factor j]
  "Given the annuity factor and premium payment frequency calculates adjusted annuity factor"
  (- annuity-factor
     (/ 
       (- j 1) 
       (* 2 j))))

(defn net-premium [net-single-premium adj-annuity-factor j] 
  (/ net-single-premium
     (* adj-annuity-factor j)))

(defn gross-single-premium [net-single-premium annuity-factor sum-insured]
  "Given the net-single-premium, annuity factor and sum insured calculates gross single premium"
  (* (/ 
       (+ 
         (/ net-single-premium sum-insured)
         (params/get-alpha)
         (* (params/get-gamma) annuity-factor)) 
       (- 1 (params/get-beta)))
     sum-insured))

(defn gross-premium [gsp adj-annuity-factor j]
  "Given the gross single premium, adjusted anuity factor and premium payment frequency j"
  (/ gsp 
     (* adj-annuity-factor j)))
  
(defn gross-premium-interface [insurance x n t j sum-insured gender]
  "Given the insurance type, age x, contract duration n, premium payment duration t, premium payment frequency, sum insured and gender calculates gross premium"
  (let [qx (get-qx (read-qx "mort-tables/q.xlsx" "Sheet1" gender))
        Mx (calc-Mx2 qx)
        Dx (calc-Dx2 qx)
        annuity-factor (annuity-factor (calc-Nx Dx) Dx x t)]
      (gross-premium
        (gross-single-premium
          (net-single-premium
            (nAx Mx Dx x n)
            (nEx Dx x n)
            insurance
            sum-insured)
          annuity-factor
          sum-insured)
        (adj-annuity-factor annuity-factor j)
        j)))
