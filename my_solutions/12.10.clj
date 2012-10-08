; Starter pieces
(def seq-zip
    (fn [tree] tree))

; Exercise 1

(defn znode [tree] (identity tree))

(defn zdown [tree] (first tree))

; Tests
(println 
    (-> 
        '(a b c)
        seq-zip
        zdown
        znode
    ) ; a

    (-> 
        '( (+ 1 2) 3 4) 
        seq-zip 
        zdown
        znode
    ) ; (+ 1 2)

    (-> 
        '( (+ 1 2) 3 4) 
        seq-zip 
        zdown 
        zdown 
        znode
    ) ; +
)
