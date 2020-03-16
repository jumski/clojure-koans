(ns koans.26-transducers
  (:require [koan-engine.core :refer :all]))

(def example-transducer
  (map inc))

(def transforms
  (comp (map inc)
     (filter even?)))

(defn my-reducing-fn
  ([] 10) ; initialization step
  ([i] i) ; completing step
  ([acc i] (+ acc i))) ; reducing step

(defn add-index-prefix-reducing-fn
  ([] [1 []])
  ([acc] (second acc))
  ([[idx res] x]
   [(inc idx) (conj res (str idx ". " x))]))

(meditations
 "A sequence operation with only one argument often returns a transducer"
 (= [2 3 4]
    (sequence example-transducer [1 2 3]))

 "Consider that sequence operations can be composed as transducers"
 (= [2 4]
    (transduce transforms conj [1 2 3]))

 "We can do this eagerly"
 (= [2 4]
    (into [] transforms [1 2 3]))

 "Or lazily"
 (= [2 4]
    (sequence transforms [1 2 3]))

 "The transduce function can combine mapping and reduction"
 (= 6
    (transduce transforms + [1 2 3]))

 "[jumski] sequence with multiple colls"
 (= '(12 14 16)
    (sequence (map #(+ %1 %2)) [1 2 3] [11 12 13]))

 "[jumski] reducing functions"
 (= 16 (transduce identity my-reducing-fn [1 2 3]))

 "[jumski] multi-arity reducing fns in transducers (transducer as 'reduce')"
 (= ["1. one" "2. two" "3. three"]
    (transduce
      identity
      add-index-prefix-reducing-fn
      ["one" "two" "three"]))

 "[jumski] transduce and sequence once again"
 (= {:sequenced [0 4 8 12 16]
     :transduced 40
     :hashmapped {0 0, 4 4, 8 8, 12 12, 16 16}}
    (let [double-all-evens (comp (filter even?)
                                 (map #(* 2 %)))
          vectorize (comp (map #(repeat 2 %)) (map vec))
          input (range 10)
          sequenced (sequence double-all-evens input)
          transduced (transduce double-all-evens + input)
          hashmapped (into {} (comp double-all-evens vectorize) input)]
      {:sequenced sequenced
       :transduced transduced
       :hashmapped hashmapped})))

;; NOTES:
;; Transducer is a fn that takes reducing-fn and returns another reducing-fn
