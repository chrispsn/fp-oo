; Starter pieces
(def seq-zip
    (fn [tree] tree))

; Exercise 1

(def znode 
    (fn [tree] (identity tree))
)

(def zdown 
    (fn [tree] (first tree))
)

; Tests
(println 
(-> 
    '(a b c)
    seq-zip
    zdown
    znode)
; a

(-> 
    '( (+ 1 2) 3 4) 
    seq-zip 
    zdown
    znode)
; (+ 1 2)

(-> '( (+ 1 2) 3 4) seq-zip zdown zdown znode)
;; +
)
