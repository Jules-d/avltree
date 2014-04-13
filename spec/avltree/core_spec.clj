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
              (should= 6 (get-in @left-leaning-5-tree [:right :right :value]))))
