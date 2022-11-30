(ns aoc2018_5
  (:require [clojure.string :as string])
  (:require [clojure.java.io :as io]))
;; 파트 1
;; 입력: dabAcCaCBAcCcaDA

;; 프로토타입 슈트를 만드는 연구실에 잠입했다. 연구소에서는 슈트를 작게 만드는데 애쓰는 중이었다.
;; 당신이라면 좀 더 잘 할 수 있을 것이다.
;; 당신이 슈트를 구성하는 화학적 구성을 살펴보니, 슈트는 매우 긴 폴리머(섬유)들로 구성되어 있는 것을 알 수 있었다.
;; 폴리머(섬유 1가닥)는 작은 유닛으로 구성되어 있다. 인접한 두 유닛이 동일한 타입이고 극성이 반대라면, 반응하여 소멸한다.
;; 유닛의 타입은 문자로 표현된다. 유닛의 극성은 대소문자로 표현된다.

;; 유닛 예)
;; - 'r'과 'R'은 타입은 동일하지만 극성은 반대이다. 반응하여 소멸할 수 있다.
;; - 'r'과 's'는 타입이 다르며, 반응하지 않는다.

;; 반응 예)
;; - "aA": 'a'와 'A'는 반응하여 소멸하며, 아무 것도 남지 않는다.
;; - "abBA": "bB"는 반응하여 소멸한다. 남은 "aA"도 반응하여 소멸하며, 아무 것도 남지 않는다.
;; - "abAB": 동일한 타입이 이웃한 경우가 없으므로, 아무 일도 일어나지 않는다.
;; - "aabAAB": "aa"와 "AA"는 동일한 타입이 이웃해 있지만 극성이 반대이므로, 아무 일도 일어나지 않는다.

;; 좀 더 복잡한 폴리머의 예) "dabAcCaCBAcCcaDA"
;; dabAcCaCBAcCcaDA  1. 처음 나오는 'cC'가 제거된다.
;; dabAaCBAcCcaDA    2. 'Aa'가 제거된다.
;; dabCBAcCcaDA      3. 'cC'나 'Cc' 중 하나가 제거된다(결과 동일).
;; dabCBAcaDA        4. 더 이상 아무 일도 일어나지 않는다.
;;
;; 가능한 모든 반응이 끝난 후에, 폴리머에는 10개(문자)의 유닛이 남았다.

;; 주어진 input 폴리머에서 모든 반응이 끝난 후, 최종으로 남는 유닛(문자)의 갯수는?

(comment
  (def polymer-sample "dabAcCaCBAcCcaDA"))

;; 전진하며 두 문자씩 비교 (sliding window) :
;;     [a*]  [*b c d e f]
;;  -> [a b*]  [*c d e f]
;;  -> [a b c*]  [*d e f]
;; -> 반응할 수 있으면 소멸 -> -1 위치로 다시 이동
;; -> 반응할 수 없으면 진행

;(defn lowercase? [s]
;  (= s (string/lower-case s)))
;(defn uppercase? [s]
;  (= s (string/upper-case s)))
;(defn same-unit [s1 s2]
;  (= (string/lower-case s1) (string/lower-case s2)))

(defn destroyable-pair? [c1 c2]
  (and (not= c1 c2)
       (= (string/lower-case c1) (string/lower-case c2))))

(comment
  (not= "a" "a") ; false
  (not= "a" "A") ; true
  (not= "a" "b") ; true
  (destroyable-pair? "a" "a") ; false
  (destroyable-pair? "a" "b") ; false
  (destroyable-pair? "a" "A") ; true
  (destroyable-pair? "A" "a") ; true
  ())

(comment
  (seq "dabAcCaCBAcCcaDA")
  (type (partition 2 (seq "dabAcCaCBAcCcaDA")))
  (vec (seq "dabAcCaCBAcCcaDA"))
  (partition 2 (seq "dabAcCaCBAcCcaDA"))
  (["a" "b" "c"] 2))

(defn load-file [filename]
  (->> filename
       (slurp)))
(def polymer-input
  (load-file "resources/day5_input.txt"))
  ;"cgGfFBbCHhxxXBEebrnNRuUMYMmyyYqTtoOQyYmDbBeYd")
  ;"dabAcCaCBAcCcaDA")
polymer-input

(comment
  (let [input (vec (seq polymer-input))]
    (reduce
      (fn [{:keys [left right]} _]
        (cond
          (= (count right) 0) (reduced (count left))          ; 끝까지 진행함
          (= (count left) 0) {:left (conj left (first right)) ; 왼쪽 집합이 고갈되어 다시 seed 투입
                              :right (rest right)}
          :else (let [c1 (last left)
                      c2 (first right)
                      destroyable (destroyable-pair? c1 c2)]
                  (if destroyable
                    {:left (vec (drop-last left))             ; 반응/소멸
                     :right (rest right)}
                    {:left (conj left c2)                     ; 전진
                     :right (rest right)}))))
      {:left (conj [] (first input))
       :right (vec (rest input))}
      (range))))



;; 파트 2
;; 주어진 문자열에서 한 유닛 (대문자와 소문자)을 전부 없앤 후 반응시켰을 때, 가장 짧은 문자열의 길이를 리턴하시오.
;; 예를 들어 dabAcCaCBAcCcaDA 에서 a/A를 없애고 모두 반응시키면 dbCBcD가 되고 길이는 6인데 비해,
;; 같은 문자열에서 c/C를 없애고 모두 반응시키면 daDA가 남고 길이가 4이므로 4가 가장 짧은 길이가 됨.
