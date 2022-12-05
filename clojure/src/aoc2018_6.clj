(ns aoc2018_6
  (:require [clojure.string :as string])
  (:import (java.util.regex Pattern))
  (:require [clojure.java.io :as io]))

;; 손목의 장치에서 삐빅 소리가 몇 번 울림. 몸이 다시 한 번 아래로 떨어지는 느낌이 남
;; 장치가 알려옴. "상황 심각. 목적지 불명. 시간 간섭이 감지됨. 새로운 좌표를 입력하세요"
;; 그리고 장치는 새로운 좌표(coordinate)의 목록을 생성했다(당신의 퍼즐 입력임). 그 좌표들은 안전한 장소일까? 위험한 장소일까?
;; 장치는 메뉴얼 720 페이지를 확인하라고 하지만, 메뉴얼이 없다.
;; 만약 그 좌표(coordinate)들이 위험하다면, 다른 지점(point)들로 부터 가장 멀리 떨어진 좌표를 찾아 위험을 최소화할 수 있을 것이다.

;; "맨하탄 거리(Manhattan distance, https://ko.wikipedia.org/wiki/%EB%A7%A8%ED%95%B4%ED%8A%BC_%EA%B1%B0%EB%A6%AC)"만을 사용해서
;; - 격자상에서 좌표를 상하좌우로 한 칸 움직이면 거리가 1이며, 이렇게 움직여 좌표A와 좌표B 사이를 이동할 수 있는 최단 거리를 말한다.
;; - 예 - (1,1) -> (1,2) : 거리 1, (1,1) -> (2,2) : 거리 2, (1,1) -> (2,3) : 거리 3
;; 각 좌표에 가장 가까운 정수 X,Y 위치의 갯수를 세어, 해당 좌표 주위의 영역을 결정하라(다른 좌표와 거리가 연결되지 않는다).
;;
;; 당신의 목표는 가장 큰 영역의 크기를 찾는 것이다(무한하지 않음).
;; 예를 들어, 다음과 같은 좌표 목록이 있다고 하자:

;; 1, 1
;; 1, 6
;; 8, 3
;; 3, 4
;; 5, 5
;; 8, 9

;; 각 좌표들을 A ~ F라고 하자. 왼쪽 상단을 0,0 으로하는 그리드에 표시할 수 있다.

;; ..........
;; .A........  <- (1, 1)
;; ..........
;; ........C.  <- (8, 3)
;; ...D......  <- (3, 4)
;; .....E....  <- (5, 5)
;; .B........  <- (1, 6)
;; ..........
;; ..........
;; ........F.  <- (8, 9)

;; 위에 표시된 뷰는 일부분이며, 실제로 그리드는 모든 방향으로 무한히 뻗어있다.
;; 맨하탄 거리를 사용하면, 각 위치(location)의 가장 가까운 좌표를 결정할 수 있으며, 소문자로 표시하면 아래와 같다.

;; aaaaa.cccc
;; aAaaa.cccc  <- A
;; aaaddecccc
;; aadddeccCc  <- C
;; ..dDdeeccc  <- D
;; bb.deEeecc  <- E
;; bBb.eeee..  <- B
;; bbb.eeefff
;; bbb.eeffff
;; bbb.ffffFf  <- F

;; '.'으로 표시된 위치들은 두 개 이상의 좌표에서 동일한 거리에 있다.
;; 그렇기 때문에 그 위치들은 어느 좌표에서도 가장 가깝다고 간주되지 않는다.
;; 이 예에서, A, B, C, F 좌표에 속한 영역들은 여기에 보이지는 않지만 무한하다. 그 영역들은 보여지는 좌표의 바깥쪽으로 무한히 뻗어있다.
;; 하지만, D, E 좌표에 속한 영역들은 유한하다. D는 9개 위치와 가장 가깝고, E는 17개 위치와 가장 가깝다(좌표의 위치도 포함).
;; 그러므로, 이 예제에서 가장 큰 영역의 크기는 17이 된다.

;; 주어진 입력으로 부터 무한하지 않은 가장 큰 영역의 크기를 구하시오.

;; 필요한 정보들
;; - coordinates: #{[x1 y1] [x2 y2] ...}
;; - grid-size: [100, 100]
;; - grid(point:closest-coordinate-or-nil): {[0 0] [a1 b1], [0 1] nil, [0 2] [a3 b7], ...}
;; - infinite-coordinates: #{[x1 y1] [x2 y2] ...}

(defn to-point [s]
  (let [[_ x y] (re-matches #"(\d*), (\d*)" s)]
    [(read-string x) (read-string y)]))

(defn load-coordinates [filename]
  (->> (io/resource filename)
       (slurp)
       (string/split-lines)
       (map to-point)
       (set)))

(defn cal-grid-size [coordinates]
  (->> (apply map max coordinates)
       (map inc)))

(defn gen-grid-points [[w h]]
  (for [x (range 0 w)
        y (range 0 h)]
    [x y]))

(defn cal-distance [[x1 y1] [x2 y2]]
  (+ (abs (- x1 x2)) (abs (- y1 y2))))


(defn find-closest-coordinate [point coordinates]
  (let [coordinate-distance (->> coordinates
                                 (map #(assoc {} % (cal-distance point %)))
                                 (into {}))
        min-distance (->> (sort-by val coordinate-distance) (first) (val))
        closest-coordinates (->> coordinate-distance (filter #(= (val %) min-distance)))
        found-count (count closest-coordinates)]
    (when (= found-count 1) (ffirst closest-coordinates))))



;(->> coordinates
;     ;(map (fn [coordinate] {coordinate (cal-distance point coordinate)}))
;     (map #(assoc {} % (cal-distance point %)))
;     (into {}))

(defn build-grid [coordinates points] ; point:closest-coordinate {[0 0] [a1 b1], [0 1] nil, [0 2] [a3 b7], ...}
  (->> (map (fn [point]
              {point (find-closest-coordinate point coordinates)})
            points)
       (into {})))


;; 그리드의 경계에 있는 위치와 연결된 좌표는 무한 확장임. 그리드의 테두리를 돌면 됨.
(defn find-infinite-coordinates [grid [w h]]
  (let [tops (for [x (range 0 w)] [x 0])
        bottoms (for [x (range 0 w)] [x (dec h)])
        lefts (for [y (range 0 h)] [0 y])
        rights (for [y (range 0 h)] [(dec w) y])
        edges (set (concat tops bottoms lefts rights))] ; 테두리
    (->> (select-keys grid edges)
         (vals)
         (keep identity) ; nil은 2개 이상의 좌표와 같은 거리
         (set))))

(defn find-largest-area [filename]
  (let [coordinates (load-coordinates filename)       ; #{[8 9] [8 3] [1 1] [3 4] [5 5] [1 6]}
        grid-size (cal-grid-size coordinates)         ; [8 9]
        grid-points (gen-grid-points grid-size)       ; ([0 0] [0 1] [0 2] ...
        grid (build-grid coordinates grid-points)     ; {[0 0] [a1 b1], [0 1] nil, [0 2] [a3 b7], ...}
        infinite-coordinates (find-infinite-coordinates grid grid-size)] ; point:coordinate #{[1 1] [8 9] ...}
    (->> grid
         (filter #(not (contains? infinite-coordinates %))) ; grid에서 infinite가 아닌 것을 제외하고
         (vals)                                             ; vals(closest coordinate)
         (keep identity)                                    ; remove nil
         (frequencies)
         (sort-by val >)                                    ; sort by freq
         (first)                                            ; min
         (val))))                                           ; freq


(comment
  (abs -1)
  (cal-distance [2 2] [1 1])
  (cal-distance [1 1] [2 2])
  (cal-distance [3 3] [1 5])
  (re-matches #"(\d*), (\d*)" "123, 456")
  (to-point "123, 456")
  (apply map max '([6 1] [2 2] [3 4]))
  (cal-grid-size '([6 1] [2 2] [3 4]))
  (def coordinates-sample
    (load-coordinates "day6_input_small.txt"))
  coordinates-sample
  (def grid-size (cal-grid-size coordinates-sample))
  (gen-grid-points grid-size)
  (->> coordinates-sample
       (map to-point)))


(comment
  (def cords #{[8 9] [8 3] [1 1] [3 4] [5 5] [1 6]})
  (find-n-distance-coordinates [1 1] 0 cords)
  (find-n-distance-coordinates [1 1] 5 cords)
  (find-n-distance-coordinates [1 1] 8 cords)
  (find-n-distance-coordinates [0 5] 2 cords) ;;;
  (find-closest-coordinate1 [0 0] cords)
  (find-closest-coordinate [0 0] cords)
  (find-closest-coordinate1 [0 1] cords)
  (find-closest-coordinate1 [0 2] cords)
  (find-closest-coordinate1 [0 3] cords)
  (find-closest-coordinate1 [0 4] cords)
  (find-closest-coordinate1 [0 5] cords) ;;;
  (find-closest-coordinate1 [0 6] cords)
  (find-closest-coordinate1 [1 1] cords)
  (find-closest-coordinate1 [4 0] cords)
  (find-closest-coordinate1 [5 0] cords)
  (find-closest-coordinate1 [6 0] cords)
  (find-closest-coordinate1 [7 0] cords)
  (find-closest-coordinate1 [5 2] cords)
  (def grid-points1 (gen-grid-points (cal-grid-size cords)))
  grid-points1
  (def grid-size1 (cal-grid-size cords))
  grid-size1
  (build-grid cords grid-points1)
  (def grid1 (build-grid cords grid-points1))
  (set (concat '([1 1] [2 2]) '([3 3] [2 2])))
  (find-infinite-coordinates [] [3 3])
  (find-infinite-coordinates grid1 grid-size1)
  (select-keys {"a" 1 "b" 2 "c" 3} ["b" "c"])
  (into {} '([1 1] [2 2]) '([3 3] [2 2]))
  (merge {"a" 1 "b" 2} {"c" 3})
  (keep identity [nil 1 2 nil 3])
  (filter (fn [[key val]]
            (complement #(contains? #{"a" "c"} key))) {"a" 1 "b" 2})
  (not (contains? #{"a" "c"} "b"))
  (not-any? #(contains? #{"a" "c"})  "b")
  (find-largest-area "day6_input_small.txt")
  (time (find-largest-area "day6_input.txt"))
  (time (find-largest-area "day6_input_small.txt"))
  (time (find-largest-area "day6_input_small2.txt"))
  ())






;; 파트 2
;; 안전(safe) 한 지역은 근원지'들'로부터의 맨하탄거리(Manhattan distance, 격자를 상하좌우로만 움직일때의 최단 거리)의 '합'이 N 미만인 지역임.

;;  ..........
;;  .A........
;;  ..........
;;  ...###..C.
;;  ..#D###...
;;  ..###E#...
;;  .B.###....
;;  ..........
;;  ..........
;;  ........F.

;; Distance to coordinate A: abs(4-1) + abs(3-1) =  5
;; Distance to coordinate B: abs(4-1) + abs(3-6) =  6
;; Distance to coordinate C: abs(4-8) + abs(3-3) =  4
;; Distance to coordinate D: abs(4-3) + abs(3-4) =  2
;; Distance to coordinate E: abs(4-5) + abs(3-5) =  3
;; Distance to coordinate F: abs(4-8) + abs(3-9) = 10
;; Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30

;; N이 10000 미만인 안전한 지역의 사이즈를 구하시오.
