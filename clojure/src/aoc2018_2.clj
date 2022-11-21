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

(defn bool-to-int [b]
  (if b 1 0))
(bool-to-int true)
(bool-to-int false)


;; map 사용
(def freq-count-map
  (reduce
    (fn [acc freq-by-char]
      (let [freq-vals (vals freq-by-char)
            {:keys [sum2 sum3]} acc
            inc2 (if (num-contains? 2 freq-vals) 1 0)
            inc3 (if (num-contains? 3 freq-vals) 1 0)]
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
               [(if (num-contains? 2 freq-by-char) 1 0)
                (if (num-contains? 3 freq-by-char) 1 0)]))
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
         [(if (contains-two? freq-by-char) 1 0)
          (if (contains-three? freq-by-char) 1 0)]))
  (apply map +)                             ; (4 3)
  (apply *))                                ; 4 * 3 = 12

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
; juxt를 사용할 경우 boolean -> int로 변경하는 함수가 별도로 필요하고, list 안의 vector를 int로 가공해 주는 절차가 필요해서 메트리가 없어 보임.


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


; "asdf" "1234" => [(a 1) (b 2) (c 3) ...] 변환할 수 있어야
(map list [1 2 3] [4 5 6])     ; ((1 4) (2 5) (3 6))
(map list '(1 2 3) '(4 5 6))   ; ((1 4) (2 5) (3 6))
(map vector '(1 2 3) '(4 5 6)) ; ([1 4] [2 5] [3 6])
(map vector [1 2 3] [4 5 6])   ; ([1 4] [2 5] [3 6])
(map vector "asdf" "qwer")     ; ([\a \q] [\s \w] [\d \e] [\f \r])

(defn count-diff-chars [[s1 s2]]
  (reduce
    (fn [sum [c1 c2]]
      (+ sum (if (= c1 c2) 0 1)))
    0
    (map vector s1 s2)))
(count-diff-chars ["aaa" "aaa"]) ; 0
(count-diff-chars ["aaa" "aab"]) ; 1
(count-diff-chars ["aaa" "abc"]) ; 2
(count-diff-chars ["aaa" "bbb"]) ; 3

(let [coll ["a" "b" "c"]]
  (.indexOf coll "c")) ; 2


(defn seq-cartesian-strings [strings]
  (for [s1 strings
        s2 strings
        :let [s1-index (.indexOf strings s1)
              s2-index (.indexOf strings s2)]
        :when (< s1-index s2-index)]
    [s1 s2]))
(seq-cartesian-strings '("aa" "bb" "cc"))  ; (["aa" "bb"] ["aa" "cc"] ["bb" "cc"])

(defn only-matched-chars [[s1 s2]]
  (reduce
    (fn [acc [c1 c2]]
      (if (= c1 c2)
        (str acc c1)
        acc))
    nil
    (map vector s1 s2)))
(only-matched-chars ["$hello123" "#hello333"]) ; "hello3"

(reduce
  (fn [acc strings]
    (if (= 1 (count-diff-chars strings))
      (reduced (only-matched-chars strings))
      nil))
  (seq-cartesian-strings '("abcde" "fghij" "klmno" "pqrst" "fguij" "axcye" "wvxyz")))


(->>
  '("abcde" "fghij" "klmno" "pqrst" "fguij" "axcye" "wvxyz")
  ;"resources/day2_input.txt" (slurp) (clojure.string/split-lines)
  (seq-cartesian-strings)                         ; (["aa" "bb"] ["aa" "cc"] ["bb" "cc"] ... )
  (map (fn [strings]                              ; (nil nil "abcd" ...)
         (when (= 1 (count-diff-chars strings))
           (only-matched-chars strings))))
  (filter (complement nil?))                      ; "abcd" ...
  (first))



;; #################################
;; ###        Refactoring        ###
;; #################################






















