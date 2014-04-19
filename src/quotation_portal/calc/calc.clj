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

(defn v-func [i]
 (/ 1 (+ 1 i)))

(def Dx [])

(def Mx [])

(defn get-qx [qx-vec-of-maps]
  "Given the vector of qx value maps returns the vector of clean qx values"
  (loop [qx-out [] qx-in qx-vec-of-maps]
    (if (empty? qx-in)
      qx-out
      (recur (conj qx-out (:q-x (first qx-in))) (rest qx-in)))))

(defn get-px [qx]
  "Given the vector of qx values retrieves the vector of px values"
  (loop [px-out [] qx-in qx]
    (if (empty? qx-in)
      px-out
      (recur (conj px-out (- 1 (first qx-in))) (rest qx-in)))))
      
(defn get-lx [px l0]
  "Given the vector of px values and the initial l0 value returns the lx values"
  (loop [lx-out [l0] px-in px]
    (if (empty? (rest px-in))
      (reverse lx-out)
      (recur (conj (seq lx-out) (* (first lx-out) (first px-in))) (rest px-in)))))
  
(defn get-dx [lx]
  "Given the vector of lx values returns the dx values"
  (loop [dx-out [] lx-in lx]
    (if (empty? (rest lx-in))
      dx-out
      (recur (conj dx-out (- (first lx-in) (first (rest lx-in)))) (rest lx-in)))))

(defn get-Dx [lx v]
 "Given the vector of lx values and the v value returns Dx values"
 (loop [Dx-out [] lx-in lx x 0]
  (if (empty? lx-in)
    Dx-out
    (recur (conj Dx-out (* (first lx-in) (math/expt v x))) (rest lx-in) (inc x)))))

(defn get-Cx [dx v]
  "Given the vector of dx values and the v value returns Cx values"
  (loop [Cx-out [] dx-in dx x 0]
    (if (empty? dx-in)
      Cx-out
      (recur (conj Cx-out (* (first dx-in) (math/expt v (+ x 1)))) (rest dx-in) (inc x)))))

(defn sum [vec]
  "Given the vector returns the sum of vector elements"
  (loop [sum 0 vec-in vec]
    (if (empty? vec-in)
      sum
      (recur (+ sum (first vec-in)) (rest vec-in)))))

(defn get-Nx [Dx]
  "Given the vector of Dx values returns Nx values"
  (loop [Nx-out [] Dx-in Dx]
    (if (empty? Dx-in)
      Nx-out
      (recur (conj Nx-out (sum Dx-in)) (rest Dx-in)))))

(defn get-Mx [Cx]
  "Given the vector of Cx values returns Mx values"
  (loop [Mx-out [] Cx-in (reverse (rest (reverse Cx)))]
    (if (empty? Cx-in)
      Mx-out
      (recur (conj Mx-out (sum Cx-in)) (rest Cx-in)))))

;;;;;;;;;;;FOR TESTING PURPOSES
(def Mx (get-Mx (get-Cx (get-dx (get-lx (get-px (get-qx (read-qx "q.xlsx" "Sheet1" "male"))) 100000)) (v-func (params/get-interest-rate)))))
(def Dx (get-Dx (get-lx (get-px (get-qx (read-qx "q.xlsx" "Sheet1" "male"))) 100000) (v-func (params/get-interest-rate))))
(def Nx (get-Nx Dx))
;;;;;;;;;

;(defn calc-Mx [workbook sheet gender sum-insured i]
;  (get-Mx (get-Cx (get-dx (get-lx (get-px (get-qx (read-qx "q.xlsx" "Sheet1" "male"))) 100000)) (v-func 0.05))))
;
;(defn calc-Dx [workbook sheet gender sum-insured i]
;  (get-Dx (get-lx (get-px (get-qx (read-qx "q.xlsx" "Sheet1" "male"))) 100000) (v-func 0.05)))


(defn nEx [x n]
  "Given the age of insured person x and the contract duration n returns nEx value"
  (/ (nth Dx (+ x n)) (nth Dx x)))

(defn nAx [x n]
  "Given the age of insured person x and the contract duration n returns nAx value"
  (/ (- (nth Mx x) (nth Mx (+ x n))) (nth Dx x)))

(defn net-single-premium [insurance x n sum-insured]
  "Given the insurance, age of the insured person x, contract duration n and sum-insured returns the net-single premium"
  (* sum-insured
     (case insurance
       "saving" (nEx x n) 
       "death" (nAx x n)
       "endownment" (+ (nEx x n) (nAx x n)) 0)))

(defn annuity-factor [x t]
  "Given the age of insured person x and premium payment duration returns annuity factor"
  (/ (- 
       (nth Nx x) (nth Nx (+ x t))) 
     (nth Dx x)))

(defn adj-annuity-factor [x t j]
  "Given the age of insured person x, premium payment duration t and premium payment frequency j returns annuity factor"
  (- (annuity-factor x t)
     (/ 
       (- j 1) 
       (* 2 j))))

(defn net-premium [insurance x n t j sum-insured]
  "Given the insurance, age of the insured person x, contract duration n, premium payment duration t, premium payment frequency j and sum-insured returns net-premium"
  (/ (net-single-premium insurance x n sum-insured)
     (* (adj-annuity-factor x t j) j)))

(defn gross-single-premium [insurance x n t sum-insured]
  (* (/ 
       (+ 
         (/ (net-single-premium insurance x n sum-insured) sum-insured)
         (params/get-alpha)
         (* (params/get-gamma) (annuity-factor x t))) 
       (- 1 (params/get-beta)))
     sum-insured))

(defn gross-premium [insurance x n t j sum-insured]
  "Given the insurance, age x, contract duration n, premiump payment duration 5, premium payment freq j, sum-insured"
  (/ (gross-single-premium insurance x n t sum-insured)
     (* (adj-annuity-factor x t j) j)))
                      

