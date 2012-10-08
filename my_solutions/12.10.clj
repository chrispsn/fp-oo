; Exercise 1

; starter pieces
(def seq-zip
    (fn [tree] tree))

; my answers
(defn znode [tree] (identity tree))

(defn zdown [tree] (first tree))

; tests
(println 
    (-> '(a b c) seq-zip zdown znode)               ; a
    (-> '( (+ 1 2) 3 4) seq-zip zdown znode)        ; (+ 1 2)
    (-> '( (+ 1 2) 3 4) seq-zip zdown zdown znode)  ; +
)


; Exercise 2

; starter pieces
(def seq-zip
    (fn [tree] {
        :here tree
        :parents '()
    })
)

; my answers
(defn znode [zipper] (:here zipper))

(defn zdown [zipper] (assoc zipper
        :here (first (:here zipper))
        :parents (:here zipper)
    )
)

; tests
(println 
    (-> '(a b c) seq-zip znode)         ; (a b c)
    (-> '(a b c) seq-zip zdown znode)   ; a
)


; TEMPLATE

; starter pieces

; my answers

; tests
