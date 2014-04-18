(ns avltree.core-spec
  (:use
   [speclj.core]
   [avltree.core])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(describe "create-tree"
    (it "should have the value we give it"
        (should= 3 (:value (create-tree 3))))
    (it ":left is null"
        (should= nil (:left (create-tree [3]))))
    (it ":right is null"
        (should= nil (:right (create-tree [3])))))

(describe "insert"
          (it "should create a new tree with a null tree"
              (should= (create-tree 3) (insert nil 3)))
          (it "should allow an initial value for convenience"
              (should= 3 (:value (insert 3 42))))
          (it "should add a smaller value on the left"
              (should= 2
                       (get-in (insert (create-tree 3) 2) [:left :value])))
          (it "should add a larger value on the right"
              (should= 4
                       (get-in (insert (create-tree 3) 4) [:right :value])))
;;; Inserting 3,2,5,1,4 should result in this arrangement:
;;;     3
;;;    / \
;;;   2  5
;;;  /  / \
;;; 1  4   6
          (with left-leaning-5-tree (-> (create-tree 3)
                                        (insert 2)
                                        (insert 5)
                                        (insert 1)
                                        (insert 4)
                                        (insert 6)))
          (it "left-left"
              (should= 1 (get-in @left-leaning-5-tree [:left :left :value])))
          (it "right-left"
              (should= 4 (get-in @left-leaning-5-tree [:right :left :value])))
          (it "right-right"
              (should= 6 (get-in @left-leaning-5-tree [:right :right :value])))
          (it "should handle multiple values"
              (should= @left-leaning-5-tree (insert 3 2 5 1 4 6)))
;          (it "should always be a binary tree"              (should= :true (validate-bst (insert (insert 3 1 4 2 6 5)))))
          )

(describe "search"
          (with balanced-odd-numbers-tree (insert 7
                                                  11 3
                                                  9 13
                                                  5 1))
          (it "returns nil for empty trees"
              (should= nil (search nil 7) ))
          (it "returns nil for values not in the tree"
              (should= nil (search @balanced-odd-numbers-tree 12)))
          (it "returns values in the tree"
              (should= 7 (search @balanced-odd-numbers-tree 7)))
          (it "returns a value on the left"
              (should= 1 (search @balanced-odd-numbers-tree 1)))
          (it "returns values anywhere in the tree"
              (should= 3 (search @balanced-odd-numbers-tree 3))))

(describe "rotate left"
          (with unrotated-left-1-2-3 {:value 1,
                                      :right {:value 2,
                                              :right {:value 3, :height 0}}})
          (with rotated-left-1-2-3 (rotate-left @unrotated-left-1-2-3))
          (it "new root node" (should= 2 (:value @rotated-left-1-2-3)))
          (it "new left node" (should= 1 (:value (:left @rotated-left-1-2-3))))
          (it "new right node" (should= 3 (:value (:right @rotated-left-1-2-3)))))

(describe "rotate right"
          (with unrotated-3-2-1 {:value 3 :left {:value 2 :left {:value 1}}})
          (with rotated-right-3-2-1 (rotate-right @unrotated-3-2-1))
          (it "new root node"
              (should= 2 (:value @rotated-right-3-2-1)))
          (it "new left node"
              (should= 1 (:value (:left @rotated-right-3-2-1))))
          (it "new right node"
              (should= 3 (:value (:right @rotated-right-3-2-1)))))

(describe "recalculate-height"
          (it "assocs 1 with no children"
              (should= 1 (:height (recalculate-height (insert 1)))))
          (it "assocs 2 with a child"
              (should= 2 (:height (recalculate-height (insert 1 2))))))

(describe "height"
          (it "height of nil is 0"
              (should= 0 (height nil)))
          (it "a new node has height 1"
              (should= 1 (height (insert 1))))
          (it "a node with a child has height 2"
              (should= 2 (height (insert 1 2)))))

(describe "balance-factor"
          (it "has a balance-factor of 1 after a left-insert"
              (should= 1 (balance-factor (insert 3 2))))
          (it "has a balance-factor of -1 after a right-insert"
              (should= -1 (balance-factor (insert 2 3))))
          (it "has a balance-factor of 0 after a left and a right insert"
              (should= 0 (balance-factor (insert 2 3 1))))
          (it "still has a balance-factor of zero after multiple balance-factord inserts"
              (should= 0 (balance-factor (insert 7 11 3 9 5 13 1))))
          (it "will rebalance-factor at 2 (left left)"
              (should= 2 (:value (insert 3 2 1)))))


(describe "balance"
          (it "handles the left-left case"
              (should= 2 (:value (insert 3 2 1)))
              (should= 1 (:value (:left (insert 3 2 1))))
              (should= 3 (:value (:right (insert 3 2 1)))))
          (it "should handle the left-right case"
              (should= 2 (:value (insert 3 1 2)))
              (should= 1 (:value (:left (insert 3 2 1))))
              (should= 3 (:value (:right (insert 3 2 1)))))
          (it "should handle the right-right case"
              (should= 2 (:value (insert 1 2 3)))
              (should= 1 (:value (:left (insert 3 2 1))))
              (should= 3 (:value (:right (insert 3 2 1)))))
          (it "should handle the right-left case"
              (should= 2 (:value (insert 1 3 2)))
              (should= 1 (:value (:left (insert 3 2 1))))
              (should= 3 (:value (:right (insert 3 2 1))))))

(describe "reduce-tree"
          (it "should pass the value and the accumulator to the function"
              (should= "1" (reduce-tree (insert 1) str "")))
          (it "should reduce into the right branch"
              (should= 3 (reduce-tree (insert 1 2) + 0)))
          (it "should reduce into the left branch"
              (should= 3 (reduce-tree (insert 2 1) + 0)))
          (it "should apply the function to each value in order and accumulate the result"
              (should= '(1 2 3 4 5 6) (reduce-tree (insert 1 2 3 4 6 5) cons []))))

;;; The test.check example
(def prop-sorted-first-less-than-last
  (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
    (let [s (sort v)]
      (<= (first s) (last s)))))

(describe "trying out test.check"
          (it "Run the example test.check in a speclj test"
              (should-be :result
               (tc/quick-check 1000 prop-sorted-first-less-than-last))))

(def input-gen
  (gen/vector gen/int))

(defn sorted-unique-values [coll]
  (-> coll
      (set)
      (vec)
      (sort)))

(describe "sorted-unique-values"
          (it "Sorts and eliminates duplicates"
              (should= [0 1 2 3 4]
                       (sorted-unique-values [ 0 1 0 2 3 3 1 0 2 4]))
              (should-not= [0 1 2 3 4]
                           (sorted-unique-values [ 0 1 0 2 5 3 3 1 0 2 4]))))

(def prop-reduce-tree-cons-equals-sorted-set
  (prop/for-all [i input-gen]
                (= (sorted-unique-values i)
                   (reduce-tree (apply insert i) cons []))))

;;; Oops!  It turns out that an assertion is truthy, so I needed to switch to
;;; (should-not-be :fail property)
(describe "My first property based test"
          (it "Check that using reduce tree on any input returns a sorted collection with no duplicates"
              (should-not-be :fail
                         (tc/quick-check 1000 prop-reduce-tree-cons-equals-sorted-set))))
