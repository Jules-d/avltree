(ns avltree.core)

(defn create-tree [value]
  {:value value :height 1})

(defn search
  [tree value]
  (cond (nil? tree) nil
        (= value (:value tree)) value
        (< value (:value tree)) (search (:left tree) value)
        (< (:value tree) value) (search (:right tree) value)
        :true :error))

(defn height [tree]
  (cond (nil? tree) 0
        (contains?  tree :height) (:height tree)
        :true (inc (max (height (:left tree))
                        (height (:right tree))))))

(defn recalculate-height [tree]
  (assoc tree :height (inc (max (height (:left tree))
                                (height (:right tree))))))

;; Reference link:
;; http://pages.cs.wisc.edu/~ealexand/cs367/NOTES/AVL-Trees/index.html
(defn rotate-left [tree]
  (let [A (:value tree)
        B (:value (:right tree))
        X (:left tree)
        Y (get-in tree [:right :left])
        Z (get-in tree [:right :right])]
    ;(println "rotating left")
    {:value B
     :left {:value A :left X :right Y}
     :right Z}))

(defn rotate-right [tree]
  (let [B (:value tree)
        A (:value (:left tree))
        Z (:right tree)
        X (:left (:left tree))
        Y (:right (:left tree))]
    ;(println "rotating right")
    (recalculate-height
     {:value A
      :left X
      :right (recalculate-height
              {:value B
               :left Y
               :right Z})})))


(defn balance-factor [tree]
  (if (nil? tree) 0 (- (height (:left tree)) (height (:right tree)))))

(defn balance [tree]
  (let [local-balance-factor (balance-factor tree)]
         ;(println (:value potentially-unbalanced-result) " balance: " balance)
    (cond (= 2 local-balance-factor) (cond
                                (= 1 (balance-factor (:left tree))) (rotate-right tree) ;; left left case
                                (= -1 (balance-factor (:left tree)))
                                (rotate-right
                                 (assoc tree :left (rotate-left (:left tree)) ))
                                :true [:rotate-right-error (balance-factor (:left tree))])
          (= -2 local-balance-factor) (if (= -1 (balance-factor (:right tree)))
                                        (rotate-left tree) ; right right case
                                        (rotate-left
                                         (assoc tree :right (rotate-right (:right tree)))))
          :true tree))

  )

(defn insert*
  "The outer insert checks that the value is not already in the tree so we know we're definitely inserting something here."
  ([tree value]
     (let [potentially-unbalanced-result
           (cond (nil? tree) (create-tree value)
                 (< value (:value tree)) (recalculate-height
                                          (assoc tree
                                            :left (insert* (:left tree) value)))
                 (< (:value tree) value) (recalculate-height
                                          (assoc tree
                                            :right (insert* (:right tree) value)))
                 :true :error)]
       (balance potentially-unbalanced-result))))

(defn insert
  ([] [])
  ([tree-or-value]
     (if (number? tree-or-value)
       (create-tree tree-or-value)
       tree-or-value))
  ([tree value]
      (cond
       (number? tree) (insert (create-tree tree) value)
       (search tree value) tree
       :true (insert* tree value)))
  ([tree value & values]
     (apply insert (cons (insert tree value) values))))

(defn reduce-tree
  "Apply f to each leaf from the rightmost to the leftmost, accumulating the result in the accumulator"
  [tree f accumulator]
  (if (or (empty? tree)
            (nil? tree)) accumulator
            (let [right-value (if (:right tree)
                                (reduce-tree (:right tree) f accumulator)
                                accumulator)
                  middle-value (f (:value tree) right-value)]
              (if (:left tree)
                (reduce-tree (:left tree) f middle-value)
                middle-value))))

(defn validate-bst [tree]
  nil)



(defn -main
    "I don't do a whole lot."
    [& args]
    println ("Hello, World!"))
