(ns avltree.core-spec
    (:use
        [speclj.core]
        [avltree.core]))

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
