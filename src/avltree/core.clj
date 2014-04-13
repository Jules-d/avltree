(ns avltree.core)

(defn create-tree [value]
  {:value value})

(defn insert
  ([tree value]
      (cond (nil? tree) (create-tree value)
            (number? tree) (insert (create-tree tree) value)
            (= value (:value tree)) tree
            (< value (:value tree)) (assoc tree :left (insert (:left tree) value))
            (< (:value tree) value) (assoc tree :right (insert (:right tree) value))
            :true :error))
  ([tree value & values]
     (apply insert (cons (insert tree value) values))))

(defn search
  [tree value]
  (cond (nil? tree) nil))

(defn -main
    "I don't do a whole lot."
    [& args]
    println ("Hello, World!"))
