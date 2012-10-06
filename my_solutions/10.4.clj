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

; Exercise 8
(def check-sum 
    (fn [digits] 
        (apply + (map * 
            (range 1 (+ (count digits) 1))
            digits
        ))
    )
)
(println (check-sum [4 8 9 3 2]))

; Exercise 9
; source from higher-order-functions.clj
(use '[clojure.string :only [split]])
(def reversed-digits
     (fn [string]
       (reverse
        (map (fn [digit-string] (Integer/parseInt digit-string))
             (rest (split string #""))))))

; my solution
(def isbn? (fn [string] (= 0 (rem (check-sum (reversed-digits string)) 11))))
(println (map isbn? ["0131774115" "0977716614" "1934356190"]))

; Exercise 10

(def check-sum 
    (fn [digits] 
        (apply + 
            (map * 
                (map (fn [x] (if (odd? x) 1 3)) 
                    (range 1 (+ (count digits) 1))
                )
            digits)
        )
    )
)
(println (check-sum [4 8 9 3 2]))

(def upc? 
    (fn [string] 
        (= 0 (rem (check-sum (reversed-digits string)) 10))
    )
)
(println (map upc? ["074182265830" "731124100023" "722252601404"]))


; Exercise 11

(def check-sum (fn [transform-fn] 
    (fn [digits] 
        (apply + 
            (map * 
                (map transform-fn
                    (range 1 (+ (count digits) 1))
                )
            digits)
        )
    )
))

(def number-checker (fn [base check-sum] 
    (fn [string] 
        (= 0 (rem (check-sum (reversed-digits string)) base))
    )
))

(def isbn? (number-checker 11 (check-sum (fn [x] x))))
(def upc? (number-checker 10 (check-sum (fn [x] (if (odd? x) 1 3)))))
(println (map isbn? ["0131774115" "0977716614" "1934356190"]))
(println (map upc? ["074182265830" "731124100023" "722252601404"]))
