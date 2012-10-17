; Exercise 1

; starter pieces
(def seq-zip
    (fn [tree] tree))

; my answers
(defn znode [tree] (identity tree))

(defn zdown [tree] (first tree))

; tests
(println 
;    (-> '(a b c) seq-zip zdown znode)               ; a
;    (-> '( (+ 1 2) 3 4) seq-zip zdown znode)        ; (+ 1 2)
;    (-> '( (+ 1 2) 3 4) seq-zip zdown zdown znode)  ; +
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
        :parents (:here zipper) ; TODO - I don't think this is right
    )
)

(defn zup [zipper] 
    (if (empty? (:parents zipper))
        nil
        (assoc zipper
            :here (:parents zipper)
            :parents '() ; TODO
        )
    )
)

(defn zroot [zipper]
    (if (empty? (:parents zipper))
        (:here zipper)
        (zroot (zup zipper))
    )
)

; tests
(println 
    (-> '(a b c) seq-zip znode)             ; (a b c)
    (-> '(a b c) seq-zip zdown znode)       ; a
    (-> '(a b c) seq-zip zdown zup znode)   ; (a b c)
    (-> '(a b c) seq-zip zup)               ; nil
    (-> '() seq-zip zdown)                  ; nil
    (-> '(a) seq-zip zroot)                 ; (a)
    (-> '(((a)) b c) seq-zip zdown zdown zdown zroot) ; (((a)) b c)
)


; TEMPLATE

; starter pieces

; my answers

; tests
