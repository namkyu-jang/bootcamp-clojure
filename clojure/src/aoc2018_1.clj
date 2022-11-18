(ns aoc2018-1
  (:require [clojure.java.io :as io]))

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

(def freq-numbers (-> "day1_input.txt"
                      (io/resource)
                      (slurp)
                      (clojure.string/split-lines)))
(reduce + (map parse-long freq-numbers))


(def sum (->> "resources/day1_input.txt"
              (slurp)
              (clojure.string/split-lines)
              (map parse-long)
              (reduce +)))
sum

;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...

(def freq-numbers (->> ;"resources/day1_small_input.txt"
                       "resources/day1_input.txt"
                       (slurp)
                       (clojure.string/split-lines)
                       (map parse-long)))
freq-numbers

(defn find-num1 [numbers]
  (loop [nums numbers
         sum 0
         results #{}]
    (let [nums (if (empty? nums) numbers nums)  ; coll이 비었으면 다시 반복
          sum (+ sum (first nums))]
      (if (results sum) ;(contains? results sum)
        sum
        (recur
          (rest nums)
          sum
          (conj results sum))))))

(find-num1 freq-numbers)


;; reduced + atom
(defn find-num2 [numbers]
  (let [sum (atom 0)]
    (reduce
      (fn [results number]
        (reset! sum (+ @sum number))
        (if (results @sum)
          (reduced @sum)
          (conj results @sum)))
      #{}
      (cycle numbers))))

(find-num2 freq-numbers)


; reduce
(defn find-num3 [numbers]
  (reduce
    (fn [{:keys [sum results]} number]
      (let [sum (+ sum number)]
        (if (results sum)
          (reduced sum)
          {:sum sum
           :results (conj results sum)})))
    {:sum 0 :results #{}}
    (cycle numbers)))

(find-num3 freq-numbers)


; find-num4
(defn find-dup [numbers]
  (let [found (reduce
                (fn [acc number]
                  (if (some #(= % number) acc)
                    (reduced number)
                    (conj acc number)))
                #{}
                numbers)]
    ;(if (reduced? found) found nil)
    (if (number? found) found nil)))

(find-dup [1 2 3 4 1]) ; 1
(find-dup [1 2 3 4 5]) ; nil


(defn accumulate-sum [numbers]
  (let [result (reduce
                 (fn [{:keys [sum sums]} number]
                   (let [new-sum (+ sum number)]
                     {:sum new-sum
                      :sums (conj sums new-sum)}))
                 {:sum 0 :sums []}
                 numbers)]
    (:sums result)))

(accumulate-sum [1 2 3 4 5 6 7 8 9 10])


(defn find-num4 [numbers]
  (->> (take 100 (cycle numbers)) ;; 무한 시퀀스가 되어야...
       (accumulate-sum)
       (find-dup)))

(find-num4 freq-numbers)



(defn seq-sums [numbers]
  (reductions
    (fn [sum number]
      (+ (or sum 0) number))
    (cycle numbers)))

(take 20 (seq-sums freq-numbers))
(count (take 20 (seq-sums freq-numbers)))

(defn find-same-number [numbers]
  (reduce
    (fn [number-set number]
      (if (number-set number)
        (reduced number)
        (conj number-set number)))
    #{}
    numbers))

(find-same-number (seq-sums freq-numbers))

(->> "resources/day1_input.txt"
     (slurp)
     (clojure.string/split-lines)
     (map parse-long)
     (seq-sums)
     (find-same-number))
