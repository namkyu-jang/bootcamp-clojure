(ns aoc2018-2
  (:require [clojure.java.io :as io]))


;; 파트 1
;; 주어진 각각의 문자열에서, 같은 문자가 두번 혹은 세번씩 나타난다면 각각을 한번씩 센다.
;; 두번 나타난 문자가 있는 문자열의 수 * 세번 나타난 문자가 있는 문자열의 수를 반환하시오.
;; 예)
;; abcdef 어떤 문자도 두번 혹은 세번 나타나지 않음 -> (두번 나오는 문자열 수: 0, 세번 나오는 문자열 수: 0)
;; bababc 2개의 a, 3개의 b -> (두번 나오는 문자열 수: 1, 세번 나오는 문자열 수: 1)
;; abbcde 2개의 b -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 1)
;; abcccd 3개의 c -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 2)
;; aabcdd 2개의 a, 2개의 d 이지만, 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 3, 세번 나오는 문자열 수: 2)
;; abcdee 2개의 e -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 2)
;; ababab 3개의 a, 3개의 b 지만 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 3)
;; 답 : 4 * 3 = 12

(defn freq-char [string]
  (reduce
    (fn [acc c]
      (assoc acc c (inc (get acc c 0))))
    {}
    string))
(freq-char "abca") ; {\a 2, \b 1, \c 1}


(def freq-list (->> ;"resources/day2_small_input.txt"
                 "resources/day2_input.txt"
                 (slurp)
                 (clojure.string/split-lines)
                 ;(map frequencies)
                 (map freq-char)))

freq-list


(defn num-contains? [coll n]
  (some #(= % n) coll))

(contains? [1 2 3 4] 3)
(contains? '(1 2 3 4) 3)


;; map 사용
(def freq-count-map
  (reduce
    (fn [acc freq-by-char]
      (let [freq-vals (vals freq-by-char)
            {:keys [sum2 sum3]} acc
            inc2 (if (num-contains? freq-vals 2) 1 0)
            inc3 (if (num-contains? freq-vals 3) 1 0)]
        {:sum2 (+ sum2 inc2)
         :sum3 (+ sum3 inc3)}))
    {:sum2 0 :sum3 0}
    freq-list))

freq-count-map
(let [{:keys [sum2 sum3]} freq-count-map]
  (* sum2 sum3))


;; vector -> reduce
(def freq-pair
  (->> ;"resources/day2_small_input.txt"
        "resources/day2_input.txt"
        (slurp)
        (clojure.string/split-lines)
        (map freq-char)                             ; {\a 1, \b 1, \c 1, \d 1, \e 1, \f 1} ...
        (map vals)                                  ; (1 1 1 1 1 1) (3 2 1) ...
        (map (fn [freq-by-char]                     ; (0 0) (1 1) (1 0) ...
               [(if (num-contains? freq-by-char 2) 1 0)
                (if (num-contains? freq-by-char 3) 1 0)]))
        (reduce (fn [[sum2 sum3] [count2 count3]]
                  [(+ sum2 count2)
                   (+ sum3 count3)]))))

(let [[count2 count3] freq-pair]
  (* count2 count3))


;; vector -> map +
(->> ;"resources/day2_small_input.txt"
     "resources/day2_input.txt"
     (slurp)
     (clojure.string/split-lines)
     (map freq-char)                           ; {\a 1, \b 1, \c 1, \d 1, \e 1, \f 1} ...
     (map vals)                                ; (1 1 1 1 1 1) (3 2 1) ...
     (map (fn [freq-by-char]                   ; (0 0) (1 1) (1 0) ...
            [(if (num-contains? freq-by-char 2) 1 0)
             (if (num-contains? freq-by-char 3) 1 0)]))
     (apply map +)                             ; (4 3)
     (apply *))                               ; 4 * 3 = 12


(map + [0 1] [1 2] [2 3])
(map + '([0 1] [1 2] [2 3]))
(apply map + '([0 1] [1 2] [2 3]))
(vec '([0 1] [1 2] [2 3]))


;; 파트 2
;; 여러개의 문자열 중, 같은 위치에 정확히 하나의 문자가 다른 문자열 쌍에서 같은 부분만을 리턴하시오.
;; 예)
;; abcde
;; fghij
;; klmno
;; pqrst
;; fguij
;; axcye
;; wvxyz

;; 주어진 예시에서 fguij와 fghij는 같은 위치 (2번째 인덱스)에 정확히 한 문자 (u와 h)가 다름. 따라서 같은 부분인 fgij를 리턴하면 됨.

;; 숫자로 만들어 차이를 구해서 차이가 1이면, 다시 한 번 차이가 0인 것만 반환

;; string comparisons give results of the distance between the first characters
(compare 'a' 'c')
(seq (char-array "asdf"))

(defn find-1distance-pair [string-coll]
  (reduce
    (fn [acc string]
      (println "acc: " acc ", string: " string)
      (rest acc))
    (rest string-coll)
    string-coll))
(find-1distance-pair '("aaa" "abc" "aac" "aab"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn distance1? [s1 s2]
  (let [distance (reduce
                   (fn [distance-sum strings]
                     (println "strings: " strings)
                     (let [s1 (first strings)
                           s2 (last strings)
                           ;c1 (first s1)
                           ;c2 (first s2)
                           ;distance (abs (- (int (first s1)) (int (first s2))))
                           distance 0
                           distance-sum (+ distance-sum distance)]
                       (println "s1: " s1 ", s2: " s2)
                       ;(println "c1: " c1 ", c2: " c2)
                       distance-sum))
                   0
                   [s1 s2])]
    (= distance 1)))

(distance1? "aaa" "aaa")
(distance1? "aaa" "aab")

(->> (char-array "aaa")
     (seq)
     (first))

(doseq [i (range (count "asdf"))]
  (doseq))

(- (int \a) (int \b))

(first "asedf")
(class (rest "asedf"))

(defn find-1distance-pair [string-coll]
  (reduce
    (fn [acc string]
      (println "acc: " acc ", string: " string)
      (for [s acc]
        (println "-----------------" s))

      (rest acc))
    (rest string-coll)
    string-coll))
(find-1distance-pair '("aaa" "abc" "aac" "aab"))


(for [s '("aaa" "abc" "aac" "aab")]
  (println "-----------------" s))

(if (distance1? s string)
  (reduced [s string])
  (println "s: " s ", string: " string))

(let [s1 string
      s2 (first acc)
      chars1 (char-array s1)
      chars2 (char-array s2)]
  (println "s1: " s1 ", s2: " s2))

((fn [acc string]
   (println "acc: " acc ", string: " string)
   (for [s acc]
     (println "-----------------" s))) '("aaa" "abc" "aac" "aab") "123")

;(for [s '("aaa" "abc" "aac" "aab")]
;  (println s))
;
;(defn find-1distance [strings]
;  (for [s1 strings s2 strings]
;    (println s1 " - " s2)))
;(find-1distance ["aaa" "aab"])



;; #################################
;; ###        Refactoring        ###
;; #################################






















