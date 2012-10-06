; TODO why do we need to use double brackets?
; Note: according to Brian's answers, you *do* need to use a 'fn' 
; in the last step which doesn't pass to anything.
; I think the reason is because you want to make explicit that
; that function able to be wrapped around.

; Exercise 1
(println 
    (-> (concat '(a b c) '(d e f))
        ((fn [a] 
            (-> (count a) odd?)
        ))
    )
)

; Exercise 2
(println
    (-> (concat '(a b c) '(d e f))
        ((fn [a] 
            (-> (count a) odd?)
        ))
    )
)
; It's the same. Not sure what it's getting at re: being 
; 'necessarily' the same.
; Edit: oh, he means breaking up the concat step as well.
; See the answers (and the discussion re: thinking of multi-arguments
as multiple one-argument functions, which is what Haskell does.)

; Exercise 3
(println
    (-> 3 
        ((fn [a] 
            (-> (+ 2 a) inc)
        ))
    )
)
