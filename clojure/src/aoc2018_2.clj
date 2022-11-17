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

(def freq-list (->> ;"resources/day2_small_input.txt"
                 "resources/day2_input.txt"
                 (slurp)
                 (clojure.string/split-lines)
                 (map frequencies)))
freq-list

;; map 사용
(def counter-map (reduce
                   (fn [acc freq]
                     (let [count-values (vals freq)
                           count2 (:two acc)
                           count3 (:three acc)
                           inc2 (if (some #(= % 2) count-values) 1 0)
                           inc3 (if (some #(= % 3) count-values) 1 0)]
                       {:two (+ count2 inc2)
                        :three (+ count3 inc3)}))
                   {:two 0 :three 0}
                   freq-list))
counter-map
(* (:two counter-map) (:three counter-map))

;; vector -> reduce
(def freq-pair (->> ;"resources/day2_small_input.txt"
                     "resources/day2_input.txt"
                     (slurp)
                     (clojure.string/split-lines)
                     (map frequencies)
                     (map vals)
                     (map (fn [freqs]
                            [(if (some #(= % 2) freqs) 1 0)
                             (if (some #(= % 3) freqs) 1 0)]))
                     (reduce (fn [acc freq]
                               [(+ (first acc) (first freq))
                                (+ (last acc) (last freq))]))))

(* (first freq-pair) (last freq-pair))

;; vector -> map +
(def freq-pair (->> ;"resources/day2_small_input.txt"
                    "resources/day2_input.txt"
                    (slurp)
                    (clojure.string/split-lines)
                    (map frequencies)                         ; {\a 1, \b 1, \c 1, \d 1, \e 1, \f 1} ...
                    (map vals)                                ; (1 1 1 1 1 1) (3 2 1) ...
                    (map (fn [freqs]                          ; (0 0) (1 1) (1 0) ...
                           [(if (some #(= % 2) freqs) 1 0)
                            (if (some #(= % 3) freqs) 1 0)]))
                    (apply map +)                             ; (4 3)
                    (apply *)))                               ; 4 * 3 = 12
freq-pair


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

;; #################################
;; ###        Refactoring        ###
;; #################################
