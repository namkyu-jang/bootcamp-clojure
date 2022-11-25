(ns aoc2018_4
  (:require [clojure.java.io :as io])
  (:require [java-time.api :as jt]))

;; 파트 1
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

;; 키워드: 가드(Guard) 번호, 자는 시간(falls asleep), 일어나는 시간(wakes up).
;; 각 가드들은 교대 근무를 시작하고 (begins shift) 졸았다가 일어났다를 반복함.
;; 위의 예시에서 10번 가드는 0시 5분에 잤다가 25분에 일어나고, 또 0시 30분에 잠들었다가 0시 55분에 깨어남.
;; 가드들에 대해서 자고 깨는 시간 정보들이 입력으로 주어짐.

;; 파트 1은 “주어진 입력에 대해서, 가장 오랜시간 잠들어있었던 가드의 ID와, 그 가드가 가장 빈번하게 잠들어 있었던 분(minute)의 곱을 구하라”
;; 만약 20번 가드가 00:10~00:36 (26m), 다음날 00:05~00:11 (11m), 다다음날 00:11~00:13 (2m) 이렇게 잠들어 있었다면,
;; "00:11"이 가장 빈번하게 잠들어 있던 시각이고, 그 때의 "분"은 11.
;; 그럼 답은 20 * 11 = 220.

;; 용어: guard/id, duty, entry/record, month-day/date, find-most-minutes, total

; 파싱해서 만들어낼 구조
; records - date(시작인 경우 00:00으로 보정), id, type(start/end)

; duties -  duty - 일자, guard, sleep[start, end-1]...   ;; 시간("00:")에 대한 정보는 제외하고, 깨어 있는 "분"만 관리하자.

; guards: id(숫자), total-minutes, 가장 많이 중복되는 분은 그냥 계산해서 찾아내는 것으로

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

(comment
  (+ 1 1)
  {"10" {:minutes '(1 2 3)}, "20" {:minutes '(1 2 3 4)}, "30" {:minutes '(3 4)}})

(comment
  (->>
    (let [{:keys [guards]} (aggregate-duty-events simple-events)
          most-slept-guard (last (sort-by #(->> % (val) (:minutes) (count)) guards))
          [guard-id {:keys [minutes]}] most-slept-guard
           most-during-minute (->> (frequencies minutes) (sort-by val >) (first) (first))]
         (* (read-string guard-id) most-during-minute))))


;; 파트 2
;; 주어진 분(minute)에 가장 많이 잠들어 있던 가드의 ID과 그 분(minute)을 곱한 값을 구하라.
