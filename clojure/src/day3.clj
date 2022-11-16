(ns day3
  (:require [clojure.java.io :as io]))

(def input (-> "day3.sample.txt"
               (io/resource)
               (slurp)
               (clojure.string/split-lines)))

(count input)

(comment
  (+ 1 2)
  (* 2 3)
  42
  12.43
  1/3
  2/4
  2/4
  (/ 4.0 2)
  (/ 1 3)
  (/ 1.0 3)
  "jam"
  type :jam
  \j
  true
  false
  nil
  (+ 1 1)
  (+ 1 (* 10 2)))

(type :jam)

(comment
  '(1 2 3 4 5)
  '(1 2 "jam" :marmalade-jar)
  '(1,2,3)
  (first '(1 2 3))
  (rest '(1 2 3))
  (last '(1 2 3)))

(comment
  (cons 1 nil)
  (cons 1 '())
  (cons 1 '(2 3))
  (cons 1 (cons 2 nil)))

(type '(1 2 3 4 5))

(comment
  [1 2 3]
  (cons 0 [1 2 3])
  (conj [1 2 3] 0)
  (conj '(1 2 3) 0)
  (nth [1 2 3] 0)
  (nth [1 2 3] 1)
  (nth [1 2 3] 3)
  (nth '(1 2 3) 0)
  (nth '(1 2 3) 2)
  (last [1 2 3])
  (last '(1 2 3))
  (count [1 2 3]))

(comment
  (conj [1 2 3] 5))

(comment
  {:key1 1 :key2 2}
  {"k1" 1 "k2" 2}
  (count {"k1" 1})
  (get {:key1 1 :key2 2} :key2)
  (get {:key1 1 :key2 2} :key3 "not exists")
  (:key2 {:key1 1 :key2 2})
  (:key3 {:key1 1 :key2 2} "not exists")
  (keys {:key1 1 :key2 2})
  (vals {:key1 1 :key2 2})
  (type (vals {:key1 1 :key2 2}))
  (assoc {:key1 1 :key2 2} :key3 3)
  (assoc {:key1 1 :key2 2} :key2 22)
  (dissoc {:key1 1 :key2 2})
  (dissoc {:key1 1 :key2 2} :key0)
  (dissoc {:key1 1 :key2 2} :key1))

(comment
  (merge [1 2 3] 4)
  (merge {:key1 1 :key2 2} {:key3 3})
  (merge {:key1 1 :key2 2} {:key2 22 :key3 3} {:key4 444}))

(comment
  #{1 2 3}
  #{1 2 2 3}
  #{:v1 :v2 :v1})

(comment
  (clojure.set/union #{1 2 3} #{2 3 4})
  (clojure.set/intersection #{1 2 3 4} #{1 3})
  (clojure.set/difference #{1 2 3} #{1})
  (set [:v1 :v2])
  (set {:k1 1 :k2 2})
  (get #{1 2 3} 4 0)
  (:v1 #{:v1 :v2})
  (#{:v1 :v2} :v2)
  (contains? #{:v1 :v2} :v1)
  (contains? {:k1 1 :k2 2} :k2)
  (contains? '(:v1 :v2) :v2)
  (contains? [:v1 :v2] :v2)
  (conj #{:v1 :v2} :v3)
  (conj #{:v1 :v2} :v2)
  (disj [1 2 3])
  (disj #{:v1 :v2} :v1))


(comment
  '(+ 1 1)
  (first '(+ 1 1))
  (type (first '(+ 1 1))))

(comment
  (def developer "Alice")
  developer
  (def developer "Alice1")
  day3/developer
  (let [developer "Alice2"]
    developer)
  developer
  (let [developer "Alice3"]
    developer))

(comment
  (defn hello [name]
    (str "Hello " name))
  (hello "hong"))

(comment
  (defn helloworld [] "hello world")
  (helloworld))

(comment
  (defn shop-for-jams [jam1 jam2]
    {:name "jam-basket"
     :jam1 jam1
     :jam2 jam2})
  (shop-for-jams "apple" "banana"))

(comment
  (fn [name] (str "hello" name))
  ((fn [name] (str "hello " name)) "world")
  ((fn [] "test"))
  ((fn [] (str "test"))))

(comment
  (def hello (fn [name] (str "hello " name)))
  (hello "hong"))

(comment
  (#(str "test function")))

(comment
  (#(str "hello " "world" "!")))

(comment
  (#(str "hello " %) "world"))

(comment
  (#(str %1 ", " %2) 1 2))

(comment
  (ns alice.favfoods)
  *ns*
  (def developer "Alice"))

*ns*
(ns alice.favfood)
*ns*
(def developer "alice2")
alice.favfood/developer
developer

(ns rabbit.favfoods
  (:require [clojure.set :as set]))
*ns*
developer
alice.favfood/developer
(def fav-food "lettuce soup")
fav-food

(clojure.set/union #{1 2 3} #{3 4 5})

(require 'clojure.set)

(set/union #{1 2 3} #{3 4 5})
(require '[rabbit.favfoods :as rf])
af/developer
(def af/developer "alicedev")
rf/fav-food

(ns wonderland
  (:require [alice.favfood :as af]))
*ns*
af/developer

(ns wonderland
  (:require [clojure.set :as s]))

(defn common-fav-foods [food1 food2]
  (let [food-set1 (set food1)
        food-set2 (set food2)
        common-foods (s/intersection food-set1 food-set2)]
    (str "Common Foods: " common-foods)))


(common-fav-foods [:jam1 :jam2] [:jam2 :jam3])



(comment
  (first [1 2 3])
  (first [:a :b :c])
  (first))

(comment
  (class true)
  (type true))

(comment
  (true? true)
  (true? 1)
  (true? "true")
  (false? false)
  (false? nil)
  (nil? nil)
  (nil? "")
  (nil? false))

(comment
  (not nil)
  (not false)
  (not 1)
  (not nil)
  (not true)
  (not :k1))

(comment
  (= 1 1)
  (= 1 2)
  (= true true)
  (= false false)
  (= nil nil)
  (= :k1 :k1)
  (= :k1 :k2)
  (= '(:k1 :drinkme) '(:drinkme :k1))
  (= '(:k1 :drinkme) '(:k1 :drinkme))
  (= [:drinkme] [:drinkme])
  (= [:drinkme :k1] [:drinkme :k1])
  (= [:k1 :drinkme] [:drinkme :k1])
  (= #{:k1 :k2} #{:k2 :k1})
  (not= :k1 :k1)
  (not= [:k1 :k2] [:k2 :k1])
  (not= [:k1 :k2] [:k1 :k2]))

(comment
  (empty? [1 2 3])
  (empty? nil)
  (empty? [])
  (not (empty? []))
  (not (seq [])))

(comment
  (class (seq [1 2]))
  (not (empty? [1]))
  (seq [1])
  (seq []))

(comment
  (every? #(> % 0) [1 2 3])
  (every? #(> % 0) [0 1])
  (every? even? [1 2 3])
  (every? even? [2 4 0])
  (every? odd? [1 3]))

(comment
  (defn drinkable [x]
    (= x :drinkme))
  (drinkable :asdf)
  (drinkable :drinkme)
  (every? drinkable [:drinkme :drinkme])
  (every? drinkable [:drinkme :drink1])
  (every? #(= % :drinkme) [:drinkme :drinkme1])
  (every? (fn [x] (= x :drinkme)) [:drinkme :drinkme1])
  (not-any? drinkable [:drinkme1 :drinkme1])
  (not-any? drinkable [:drinkme1 :drinkme])
  (not-every? drinkable [:drinkme1 :drinkme])
  (some drinkable [:drinkme :drinkme1])
  (some drinkable [:drinkme1])
  (some #(> % 3) [1 2 3 4]))

(comment
  (#{1 2 3 4} 3)
  (some #{3} [1 2 3 4])
  (some #{3 4} [1 2 4 3]))


(comment
  (def n 3)
  (if (> n 0) "positive" "negative")
  (if true "true case" "false case")
  (if false "true case" "false case"))

(comment
  (let [positive (> -5 0)]
    (if positive "pos" "neg"))
  (def n -3)
  (if-let [positive (> n 0)]
    (str "pos " n)
    (str "neg " n)))

(comment
  (def n 3)
  (when (> n 0) "pos"))

(defn drink [b]
  (when b "drink bottle"))
(drink true)
(drink false)

(def n 3)
(when-let [drinkable (> n 0)]
  (str "drink" n))

(let [bottle "drinkme"]
  (cond
    (= bottle "poison") "don't drink"
    (= bottle "drinkme") "drink"
    :else "empty"))

(let [bottle "poison"]
  (case bottle
    "poison" "don't drink"
    "drinkme" "drink"
    "empty" "all gone"
    "unknown"))

(let [[color size  :as original] ["red" "small"]]
  (str "color: " color ", size: " size ", original: " original))

(let [{flower :flower color :color flower2 :flower2 :or {flower2 "missing"} :as all-flowers} {:flower "rose" :color "red"}]
  (str "flower: " flower ", color: " color ", flower2: " flower2 ", all: " all-flowers))

(let [{:keys [flower1 flower2]} {:flower1 "rose" :flower2 "daisy"}]
  (str flower1 ", " flower2))

(defn flower-colors [colors]
  (str "flower1: "
       (:flower1 colors)
       ", flower2: "
       (:flower2 colors)))
(flower-colors {:flower1 "rose" :flower2 "daisy"})

(range 0 3)
(range 3)
(take 10 (range))
(class (range 5))

(count (take 10 (range)))

(repeat 3 "rabbit")
(take 5 (repeat "rabbit"))
(class (repeat 3 "rabbit"))

(rand-int 3)

(repeat 3 (rand-int 10))
(repeatedly 5 #(rand-int 10))
(take 10 (repeatedly #(rand-int 10)))

(take 9 (cycle [1 2 3]))
(take 3 (rest (cycle [1 2 3])))


(def size [1 2 3 4 5])

(defn print-size [sizes out]
  (if (empty? sizes)
    out
    (print-size
      (rest sizes)
      (conj out (str "size: " (first sizes))))))
(print-size size [])

(defn print-size-loop [input]
  (loop [in input
         out []]
    (if (empty? in)
      out
      (recur
        (rest in)
        (conj out (str "size: " (first in)))))))
(print-size-loop size)

(map #(str "size: " %) size)
(class (map #(str "size: " %) size))
(take 2 (map #(str "size: " %) size))

(println "hello")


(def print-animal
  (map #(println %) animals))

print-animal

(def print-animal
  (doall (map #(println %) animals)))
print-animal

(def animals ["mouse" "duck" "dodo"])
(def colors ["brown" "black" "blue"])

(defn gen-animal-string [animal color]
  (str color "-" animal))

(map gen-animal-string animals colors)

(reduce + [1 2 3 4 5])
(reduce #(+ %1 %2) [1 2 3 4 5])
(reduce (fn [n1 n2] (+ n1 n2)) [1 2 3 4 5])
(reduce + 10 [1 2 3 4 5])

(def animals ["mouse" nil "duck" "dodo"])

(reduce
  (fn [result animal]
    (if (nil? animal) (conj result "empty") (conj result animal)))
  []
  animals)

(filter (complement nil?) animals)

(remove nil? animals)

(for [animal ["mouse" "rabbit" "duck"]]
  (str animal "!"))

(vec '(1 2 3))
(into [] '(1 2 3))

(partition 3 [1 2 3 4 5 6 7 8 9 0])
(partition-all 3 [1 2 3 4 5 6 7 8 9 0])
(partition-by #(> % 5) [1 2 3 4 5 6 7 8 9 10])