(ns poker-hand-evaluator.core-test
  (:require [clojure.test :refer :all]
            [poker-hand-evaluator.core :refer :all]))

(defn- base2-str [x] (Integer/toString (int x) 2))

(deftest deck-basics
  (testing "Contains 52 cards"
    (is (= 52 (count deck))))
  (testing "Cards are standard"
    (is (= (set (for [s '("c" "h" "s" "d") f '("2" "3" "4" "5" "6" "7" "8" "9" "T" "J" "Q" "K" "A")] (str f s)))
          (set (keys deck)))))
  (testing "Kd is represented with correct bit pattern"
    (is (= "1000000000000100101100100101" (base2-str (deck "Kd")))))
  (testing "5s is represented with correct bit pattern"
    (is (= "10000001001100000111" (base2-str (deck "5s")))))
  (testing "Jc is represented with correct bit pattern"
    (is (= "10000000001000100100011101" (base2-str (deck "Jc"))))))

(defn hand
  [rank hand-name result]
  (and (= rank (result :rank)) (= hand-name (result :hand)))
  )

(defn hand-and-cards
  [rank hand-name best-cards result]
  (and (hand rank hand-name result) (= best-cards (result :cards)))
  )

(deftest evaluation
  (testing "Straight Flush"
    (is (hand  1 :StraightFlush (evaluate "Tc" "Jc" "Qc" "Kc" "Ac")))
    (is (hand 10 :StraightFlush (evaluate "Ac" "2c" "3c" "4c" "5c")))
    )
  (testing "Four of a kind"
    (is (hand 11 :FourOfAKind (evaluate "Kc" "Ac" "Ah" "Ad" "Ac")))
    (is (hand 166 :FourOfAKind (evaluate "3c" "2c" "2h" "2d" "2c")))
    )
  (testing "Full House"
    (is (hand 167 :FullHouse (evaluate "Kc" "Kh" "Ah" "Ad" "Ac")))
    (is (hand 322 :FullHouse (evaluate "3c" "3h" "2h" "2d" "2c")))
    )
  (testing "Flush"
    (is (hand 323 :Flush (evaluate "9c" "Jc" "Qc" "Kc" "Ac")))
    (is (hand 1599 :Flush (evaluate "2c" "3c" "4c" "5c" "7c")))
    )
  (testing "Straight"
    (is (hand 1600 :Straight (evaluate "Tc" "Jc" "Qd" "Kh" "Ac")))
    (is (hand 1609 :Straight (evaluate "Ac" "2c" "3d" "4h" "5c")))
    )
  (testing "Three of a Kind"
    (is (hand 1610 :ThreeOfAKind (evaluate "Qc" "Kh" "Ah" "Ad" "Ac")))
    (is (hand 2467 :ThreeOfAKind (evaluate "4c" "3h" "2h" "2d" "2c")))
    )
  (testing "Two Pairs"
    (is (hand 2468 :TwoPairs (evaluate "Qc" "Kc" "Kd" "Ah" "Ac")))
    (is (hand 3325 :TwoPairs (evaluate "4c" "3c" "3d" "2h" "2c")))
    )
  (testing "One Pair"
    (is (hand 3326 :OnePair (evaluate "Jc" "Qd" "Kd" "Ah" "Ac")))
    (is (hand 6185 :OnePair (evaluate "2c" "2d" "3d" "4h" "5c")))
    )
  (testing "Highest Card"
    (is (hand 6186 :HighCard (evaluate "9c" "Jd" "Qd" "Kh" "Ac")))
    (is (hand 7462 :HighCard (evaluate "2c" "3d" "4d" "5h" "7c")))
    )
  )

(deftest evaluation-more-than-5-cards
  (testing "Finds highest straight out of 7 cards"
    (is (hand-and-cards 1600 :Straight '("Tc" "Jc" "Qd" "Kh" "Ac")
          (evaluate "8c" "9d" "Tc" "Jc" "Qd" "Kh" "Ac"))))
  (testing "Finds highest straight out of 7 cards in reverse order"
    (is (hand-and-cards 1600 :Straight '("Ac" "Kd" "Qc" "Jc" "Td")
          (evaluate "Ac" "Kd" "Qc" "Jc" "Td" "9h" "8c"))))
  (testing "Finds highest straight out of 6 cards"
    (is (hand-and-cards 1600 :Straight '("Tc" "Jc" "Qd" "Kh" "Ac")
          (evaluate "9d" "Tc" "Jc" "Qd" "Kh" "Ac"))))
  )
