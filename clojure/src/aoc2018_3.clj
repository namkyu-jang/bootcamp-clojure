(ns aoc2018_3
  (:require [clojure.java.io :as io]))


;; 파트 1
;; 다음과 같은 입력이 주어짐.

;; #1 @ 1,3: 4x4
;; #2 @ 3,1: 4x4
;; #3 @ 5,5: 2x2

;; # 뒤에 오는 숫자는 ID, @ 뒤에 오는 숫자 쌍 (a, b)는 시작 좌표, : 뒤에 오는 (c x d)는 격자를 나타냄.
;; 입력의 정보대로 격자 공간을 채우면 아래와 같이 됨.

;;      ........
;;      ...2222.
;;      ...2222.
;;      .11XX22.
;;      .11XX22.
;;      .111133.
;;      .111133.
;;      ........

;; 여기서 XX는 ID 1, 2, 3의 영역이 두번 이상 겹치는 지역.
;; 겹치는 지역의 갯수를 출력하시오. (위의 예시에서는 4)

;; 자료구조: map - key: 좌표, value: 카운트 -> values가 1보다 큰 갯수를 filter -> count
(comment
  (array-map)
  (class (array-map))
  (array-map :a 10)                 ; {:a 10}
  (array-map :a 10 :b 20)           ; {:a 10, :b 20}
  (apply array-map [:a 10 :b 20])   ; {:a 10, :b 20}
  (keys (array-map :a 10 :b 20))    ; (:a :b)
  (vals (array-map :a 10 :b 20))    ; (10 20)
  (array-map [0 1] 10 [0 2] 11 [0 3] 13)                  ; {[0 1] 10, [0 2] 11, [0 3] 13}
  (assoc (array-map [0 1] 10 [0 2] 11 [0 3] 13) [0 2] 12) ; {[0 1] 10, [0 2] 12, [0 3] 13}
  (assoc {} [0 0] 0 [0 1] 1 [0 2] 2)                      ; {[0 0] 0, [0 1] 1, [0 2] 2}
  #_(println))

(comment
  (re-find #"\d+" "foo 123 bar") ; 123
  (re-find #"\d+" "foobar") ; nil
  (re-matches #"(@\w+)\s([.0-9]+)%" "@shanley 19.8%") ; ["@shanley 19.8%" "@shanley" "19.8"]
  (re-matches #"(\d+)\s(\d+)\s(\d+)" "123 456 789")
  (re-matches #"#(\d+)\s@\s(\d+),(\d+):\s(\d+)x(\d+)" "#1 @ 1,3: 4x4")
  (let [[whole id x y x-size y-size] (re-matches #"#(\d+)\s@\s(\d+),(\d+):\s(\d+)x(\d+)" "#1 @ 1,3: 4x4")]
    (println (format "whole: [[%s]], id: %s, [x y]: [%s %s], %s x %s" whole id x y x-size y-size)))
  (int "1")
  (Integer/parseInt "1")
  #_(println))

(defn parse-claim
  "\"#1 @ 1,3: 4x4\" 형식의 문자열을 파싱한다"
  [s]
  (let [[_ id x y w h]
        (re-matches #"#(\d+)\s@\s(\d+),(\d+):\s(\d+)x(\d+)" s)]
    {:id id
     :x  (Integer/parseInt x)
     :y  (Integer/parseInt y)
     :w  (Integer/parseInt w)
     :h  (Integer/parseInt h)}))

(comment
  (parse-claim "#1 @ 1,3: 4x4"))

(comment
  (->> (range 1 (inc 4))
       (map (fn [index]
              [1 index])))
  (->> (for [x (range 0 4)
             y (range 11 13)]
         [x y])
       (map (fn [[x y]]
              {[x y] 1})))
  #_(println))

(defn gen-claimed-positions [{:keys [x y w h]}]
  (for [pos-x (range x (+ x w))
        pos-y (range y (+ y h))]
    [pos-x pos-y]))
(comment
  (gen-claimed-positions {:x 1 :y 4 :w 2 :h 3}))

(defn add-positions
  "claim에 :position을 추가한 {:id \"1\", :x 1, :y 3, :w 4, :h 4, :positions ([1 3] [1 4] ...)} 형태의 맵을 반환한다."
  [claim]
  (assoc claim :positions (gen-claimed-positions claim)))

(defn mark-fabrics
  "data structure: {:claims ({:id \"1\", :x 1, :y 1, :w 2, :h 2, :positions ([1 1] [1 2] ...)}...) :fabrics ({[1 3] 1, [1 4] 2, [1 5] 3, [2 3] 1, ...})}"
  [claims]
  (->> claims
       (mapcat :positions)
       (frequencies)
       (#(assoc nil :claims claims :fabrics %))))

(comment
  (->> '({:id "1", :x 1, :y 3, :w 4, :h 4,
          :positions ([1 3] [1 4] [1 5] [1 6] [2 3] [2 4] [2 5] [2 6] [3 3] [3 4] [3 5] [3 6] [4 3] [4 4] [4 5] [4 6])}
         {:id "2", :x 3, :y 1, :w 4, :h 4,
          :positions ([3 1] [3 2] [3 3] [3 4] [4 1] [4 2] [4 3] [4 4] [5 1] [5 2] [5 3] [5 4] [6 1] [6 2] [6 3] [6 4])}
         {:id "3", :x 5, :y 5, :w 2, :h 2,
          :positions ([5 5] [5 6] [6 5] [6 6])})
       (mark-fabrics)))

(defn load-claims
  "data structure: {:claims ({:id \"1\", :x 1, :y 1, :w 2, :h 2, :positions ([1 1] [1 2] ...)}...) :fabrics ({[1 3] 1, [1 4] 2, [1 5] 3, [2 3] 1, ...})}"
  [filename]
  (->> filename
       (slurp)
       (clojure.string/split-lines)
       (map parse-claim)              ; ({:id "1", :x 1, :y 3, :w 4, :h 4} {:id "2", :x 2 ...}...)
       (map add-positions)            ; ({:id "1", :x 1, :y 3, :w 4, :h 4, :positions ([1 3] [1 4] [1 5]) ...
       (mark-fabrics)))               ; {:claims ({:id "1", :x 1, :positions (...) ...} ...) :fabrics ({[1 3] 1, [1 4] 2, [1 5] 3, [2 3] 1, ...})}


(comment
  (->> (load-claims "resources/day3_small_input.txt") ; {:claims ({:id 1, :x 1, :y 1, :w 2, :h 2, :positions ([1 1] [1 2] ...)}...) :fabrics ({[1 3] 1, [1 4] 2, [1 5] 3, [2 3] 1, ...})}
       (:fabrics)                     ; ({[1 3] 1, [1 4] 2, [1 5] 3, [2 3] 1, ...})
       (vals)
       (filter #(> % 1))
       (count)))                      ; simple: 4, aoc: 110827



;; 파트 2
;; 입력대로 모든 격자를 채우고 나면, 정확히 한 ID에 해당하는 영역이 다른 어떤 영역과도 겹치지 않음
;; 위의 예시에서는 ID 3 이 ID 1, 2와 겹치지 않음. 3을 출력.
;; 겹치지 않는 영역을 가진 ID를 출력하시오. (문제에서 답이 하나만 나옴을 보장함)

(defn find-not-overlapped-positions
  "data structure: ([4 3] [2 3] ...)"
  [fabrics]
  (->> fabrics          ; {[4 3] 2, [2 3] 1, ...
       (filter (fn [[_ val]] (= val 1)))
       (map key)))      ; ([4 3] [2 3] ...

(comment
  (def claims-fabrics
    {:claims '({:id "1", :x 1, :y 3, :w 4, :h 4,
                :positions ([1 3] [1 4] [1 5] [1 6] [2 3] [2 4] [2 5] [2 6] [3 3] [3 4] [3 5] [3 6] [4 3] [4 4] [4 5] [4 6])}
               {:id "2", :x 3, :y 1, :w 4, :h 4,
                :positions ([3 1] [3 2] [3 3] [3 4] [4 1] [4 2] [4 3] [4 4] [5 1] [5 2] [5 3] [5 4] [6 1] [6 2] [6 3] [6 4])}
               {:id "3", :x 5, :y 5, :w 2, :h 2,
                :positions ([5 5] [5 6] [6 5] [6 6])}),
     :fabrics {[4 3] 2, [2 3] 1, [2 5] 1, [3 3] 2, [5 4] 1, [6 3] 1, [3 4] 2, [4 2] 1, [6 6] 1, [5 3] 1, [6 5] 1, [4 1] 1, [5 2] 1, [4 6] 1, [1 4] 1, [1 3] 1, [1 5] 1, [6 4] 1, [5 1] 1, [6 1] 1, [5 6] 1, [5 5] 1, [2 4] 1, [3 6] 1, [4 5] 1, [3 1] 1, [1 6] 1, [4 4] 2, [2 6] 1, [6 2] 1, [3 5] 1, [3 2] 1}})
  (find-not-overlapped-positions (:fabrics claims-fabrics)))


(defn all-contains? [list full-list]
  (clojure.set/subset? (set list) (set full-list)))

(comment
  (clojure.set/subset? (set '([1 1])) (set '([1 1] [2 3])))
  (all-contains? '([1 1]) '([1 1] [2 3])))


(comment
  (let [{:keys [claims fabrics]} (load-claims "resources/day3_small_input.txt") ; "resources/day3_input.txt"
        not-overlapped-positions (find-not-overlapped-positions fabrics)]
    (->> claims                                        ; ({:id 1, :x 1, :y 1, :w 2, :h 2, :positions ([1 1] [1 2] ...)}...)
         (filter #(all-contains? (:positions %) not-overlapped-positions))
         (first)                                       ; {:id "3", :x 5, :y 5, :w 2, :h 2, :positions ([5 5] [5 6] [6 5] [6 6])}
         (:id))))


