(use 'clojure.algo.monads)

(def sequence-monad-decider
     (fn [step-value continuation]
       (mapcat continuation step-value)))

(def sequence-monad-monadifier list)

(def sequence-monad
     (monad [m-result sequence-monad-monadifier
             m-bind sequence-monad-decider]))

(prn
 (with-monad sequence-monad
   (domonad [a [1 2]
             b [10, 100]
             c [-1 1]]
            (* a b c))))

(prn 
 (with-monad sequence-monad
   (domonad [a (list 1 2 3)
             b (list (- a) a)
             c (list (+ a b) (- a b))]
            (* a b c))))


; my code

;(def maybe-sequence-monad-decider
;     (fn [step-value continuation]
;       (mapcat continuation step-value)))

(def maybe-sequence-monad-decider
; note after reading solution: no need to put
; continuation into the function inputs,
; as it's in scope from its parent (best word?) function
     (fn [step-value continuation]
        (let [maybe-mod (fn [continuation value]
            (if (nil? value)
                (list nil)
                (continuation value)))]
        (mapcat (partial maybe-mod continuation) step-value))))

(def maybe-sequence-monad
     (monad [m-result sequence-monad-monadifier
             m-bind maybe-sequence-monad-decider]))

; tests

(println (with-monad maybe-sequence-monad
    (domonad [a [1 2 3]
              b [-1 1]]
              (* a b)
    )
)) ; (-1 1 -2 2 -3 3)


(println (with-monad maybe-sequence-monad
    (domonad [a [1 nil 3]
              b [-1 1]]
              (* a b))
)) ; (-1 1 nil -3 3)
