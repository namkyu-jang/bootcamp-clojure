(ns aoc2018_4
  (:require [clojure.java.io :as io])
  (:require [java-time.api :as jt]))

;; 당신은 연구실에 침입해야 한다. 하지만 연구실 앞에는 가드가 있다.
;; 당신은 과거에 연구실에 침입한 사람이 관찰하여 남긴 가드들의 근무기록을 발견하였다.
;; 자정에 근무하는 가드(Guard)는 1명이고,
;; 가드는 근무교대(begins shift)를 한 후 졸기 시작했다가(falls asleep) 깨었다(wakes up)를 반복하였다.

;; 입력:

;; [1518-11-01 00:00] Guard #10 begins shift
;; [1518-11-01 00:05] falls asleep
;; [1518-11-01 00:25] wakes up
;; [1518-11-01 00:30] falls asleep
;; [1518-11-01 00:55] wakes up
;; [1518-11-01 23:58] Guard #99 begins shift
;; [1518-11-02 00:40] falls asleep
;; [1518-11-02 00:50] wakes up
;; [1518-11-03 00:05] Guard #10 begins shift
;; [1518-11-03 00:24] falls asleep
;; [1518-11-03 00:29] wakes up
;; [1518-11-04 00:02] Guard #99 begins shift
;; [1518-11-04 00:36] falls asleep
;; [1518-11-04 00:46] wakes up
;; [1518-11-05 00:03] Guard #99 begins shift
;; [1518-11-05 00:45] falls asleep
;; [1518-11-05 00:55] wakes up

;; 위의 예시에서 10번 가드는 0:05에 졸기 시작했다가 00:25에 깨어났고, 또 0:30에 졸기 시작했다가 0:55에 깨어났다.
;; 다음은, 위의 기록을 시각적으로 표현하였다. 잠들어 있었던 시각(분)을 '#'으로 표시했다. 깨어난 시각은 잠든 시간에 포함되지 않는다.

;; Date   ID   Minute
;;             000000000011111111112222222222333333333344444444445555555555
;;             012345678901234567890123456789012345678901234567890123456789
;; 11-01  #10  .....####################.....#########################.....
;; 11-02  #99  ........................................##########..........
;; 11-03  #10  ........................#####...............................
;; 11-04  #99  ....................................##########..............
;; 11-05  #99  .............................................##########.....

;; 특정 시각에 잠들어 있을 가능성이 가능 높은 가드를 알 수 있다면, 가드를 속여 침입할 가능성을 높일 수 있을 것이다.
;; 당신은 두 가지 전략을 선택할 수 있다.
;;
;; 전략1: 가장 오랜 시간 잠든 가드를 찾는다. 그 가드가 가장 많이 잠들었던 시각(분)은?

;; 파트 1: “주어진 입력에 대해서, 가장 오랜시간 잠들어 있었던 가드의 ID와, 그 가드가 가장 빈번하게 잠들어 있었던 분(minute, 시각 중 분을 의미)의 곱을 구하라”
;; 만약 20번 가드가 00:10~00:36 (26분 동안), 다음날 00:05~00:11 (11분 동안), 다다음날 00:11~00:13 (2분 동안) 이렇게 잠들어 있었다면,
;; "00:11"이 가장 빈번하게 잠들어 있던 시각이고, 그 때의 "분"은 11이다.
;; 그럼 답은 가드ID * 분 = 20 * 11 = 220.


(defn load-file [filename]
  (->> filename
       (slurp)
       (clojure.string/split-lines)))
(comment
  (load-file "resources/day4_small_input.txt"))

(def record-re #"\[(\d{4}-\d{2}-\d{2}\s\d{2}:\d{2})\]\s(.*)")
(def event-dt-format "yyyy-MM-dd HH:mm")
(def shift-re #"Guard #(\d+) begins shift")

(comment
  (def sample-record "[1518-11-01 23:58] Guard #99 begins shift")
  (re-matches #"\[\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}\]\s.*" "[1518-11-01 00:00] asfsdf saf asdf")
  (re-matches record-re sample-record)
  (re-matches shift-re "Guard #10 begins shift"))


(defn parse-event-type [event-str] ; :type - :shift, :start, :end
  (cond
    (= event-str "falls asleep") :start
    (= event-str "wakes up") :end
    :else :shift))

(comment
  (parse-event-type "Guard #10 begins shift")
  (parse-event-type "falls asleep")
  (parse-event-type "wakes up"))


(defn parse-guard-id [event-type event-str] ; event-str: "Guard #10 begins shift"
  (when (= event-type :shift)
    (last (re-matches shift-re event-str))))

(comment
  (jt/local-date-time event-dt-format "1518-11-05 00:03")
  (def sample-dt1 (jt/local-date-time "yyyy-MM-dd HH:mm" "1518-11-05 00:03"))
  (def sample-dt2 (jt/local-date-time "yyyy-MM-dd HH:mm" "1518-11-01 23:58"))
  (jt/plus sample-dt1 (jt/hours 1))
  (jt/plus sample-dt2 (jt/hours 1))
  (jt/local-date (jt/plus sample-dt1 (jt/hours 1)))
  (jt/truncate-to (jt/plus sample-dt2 (jt/hours 1)) :days)
  (jt/fields sample-dt1)
  (jt/as sample-dt1 :minute-of-hour))

(comment
  (parse-guard-id :shift "Guard #10 begins shift")
  (parse-guard-id :start "falls asleep")
  (parse-guard-id :end "wakes up"))


(defn round-shift-dt-to-midnight [event-type dt] ; 오차 범위가 ±1 시간 범위라고 간주함
  (if (= event-type :shift)
    (jt/truncate-to (jt/plus dt (jt/hours 1)) :days)
    dt))

(comment
  (round-shift-dt-to-midnight :shift (jt/local-date-time 1518 11 01 23 58))
  (round-shift-dt-to-midnight :start  (jt/local-date-time 1518 11 01 23 58))
  (round-shift-dt-to-midnight :end  (jt/local-date-time 1518 11 01 23 58)))


(defn parse-record [s]
  (let [[_ dt-str event-str] (re-matches record-re s)
        event-type (parse-event-type event-str)
        guard-id (parse-guard-id event-type event-str)
        dt (jt/local-date-time event-dt-format dt-str)
        event-dt (round-shift-dt-to-midnight event-type dt)
        event-day (jt/local-date event-dt)
        event-minute (jt/as event-dt :minute-of-hour)]
    {:day event-day
     :minute event-minute
     :id guard-id
     :event-type event-type}))

(comment
  (parse-record "[1518-11-03 00:05] Guard #10 begins shift")
  (parse-record "[1518-11-03 00:24] falls asleep")
  (parse-record "[1518-11-03 00:29] wakes up"))

(defn aggregate-duty-events [events]
  (reduce
    (fn [{:keys [shifts guards]}
         {:keys [day minute id event-type]}]
      (let [last-shift (last shifts)
            last-shift-index (dec (count shifts))
            last-sleep-start-minute (last (:sleeps last-shift))
            last-guard-id (:id last-shift)
            guard-id (if (not= event-type :shift) last-guard-id id)
            guard (get guards guard-id)]
        (cond
          (= event-type :shift) {:shifts (conj shifts {:day day, :id guard-id, :sleeps []})
                                 :guards guards}
          (= event-type :start) {:shifts (update-in shifts [last-shift-index :sleeps] conj minute)
                                 :guards guards}
          (= event-type :end) {:shifts (update-in shifts [last-shift-index :sleeps] conj minute)
                               :guards (assoc guards guard-id (update guard :minutes concat (range last-sleep-start-minute minute)))})))
    {:shifts [], :guards {}}
    events))


(comment
  (def simple-events (->> (load-file "resources/day4_small_input.txt")
                          (map parse-record)))
  simple-events
  (aggregate-duty-events simple-events)
  (range 10 15)
  (conj nil (range 10 15))
  (conj [1 2] (range 10 15))
  (concat [1 2] (range 10 15))
  (not= "abc" "abc")
  (not= "abc" "ccc")
  (assoc [1 2 3] 1 5)
  (update [1 2 3] 1 + 1)
  (contains? #{"aaa" "bbb"} "aaa")
  (contains? #{"aaa" "bbb"} "ccc")
  (sort-by (fn [e]
             (println "e:" e " - val:" (count (:minutes (val e))))
             1)
           {"10" {:minutes '(1 2 3)}, "20" {:minutes '(1 2 3 4)}, "30" {:minutes '(3 4)}}))

(def events
  (->> (load-file "resources/day4_input.txt")
       ;(load-file "resources/day4_small_input.txt")
       (sort) ; 파일이 시간순으로 되어 있지 않아 정렬함
       (map parse-record)))

(comment
  (->>
    (let [{:keys [guards]} (aggregate-duty-events events)
          most-slept-guard (last (sort-by #(->> % (val) (:minutes) (count)) guards))
          [guard-id {:keys [minutes]}] most-slept-guard
           most-during-minute (->> (frequencies minutes) (sort-by val >) (ffirst))]
         (* (read-string guard-id) most-during-minute))))


;; 파트 2
;; 전략 2: 모든 가드들 중에서, 동일한 시각(분)에 가장 자주 잠든 가드는?
;; 의의 예시에서, 가드 99는 00:45에 다른 어떤 가드보다 잠을 자주 잤다(총 3회).
;; 다른 경비원들이 동일한 시각(분)에 가장 빈번하게 잠들었던 경우는 기껏해야 2번이다(10번 가드는 00:24에 2회 잠들었음).
;;
;; 찾아낸 가드의 ID와 분(시각 중 분)을 곱한 값은? (예시에서 99 * 45 = 4455)

