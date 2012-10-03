; Exercise 1
(println (map (partial + 2) [1 2 3]))

; Exercise 2
(def separate 
    (fn [pred sequence]
        ((juxt (partial remove pred) (partial filter pred)) sequence)
    )
)
(println (separate odd? [1 2 3 4]))

; or  ; EDIT: wow, this was way off
;(def separate 
;    (fn [pred sequence]
;        ((juxt (map partial [remove apply] pred)) sequence)
;    )
;)
;(println (separate odd? [1 2 3 4]))

; Exercise 3
; guessed in head and checked solution - was same

; Exercise 4
(fn [x] (fn [] x)) ;was almost there...

; Exercise 5 
; right idea but not what was intended by the exercise - 
; wanted to MAKE functions, not make one function which can take
; functions as inputs
;(def make-sender 
;    (fn [func instance message & args]
;        (apply-message-to (func instance)
;            instance message args)
;    )
;)

; Exercise 6
; I thought I had the answer but misread the qn - it's asking you to set
; the value of ANY ATOM you give it to 33.

; Exercise 7
(def always (fn [x] (fn [& args] x)))
(println ((always 8) 1 'a :foo))
; originally was wondering why the above different to 
; (def always (fn [x] (fn [& args] (x))));
; answer: clojure was interpreting x as a function,
; since it was the first (only) item in the list.


; keep going from Exercise 8.
