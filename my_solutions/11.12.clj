(use 'clojure.algo.monads)
(use 'clojure.set)

; Exercise 1
(def multiples (fn [x] 
    (range (* x 2) 101 x)))

(println (multiples 4))

; Exercise 2
(def non-primes (with-monad sequence-m (m-lift 1 multiples)))
(println (flatten (non-primes (range 1 101))))

; Exercise 3
(println (sort (let [all-numbers (range 1 101)
               non-primes (flatten (non-primes (range 2 101)))]
         (difference (set all-numbers) (set non-primes))
)))
; Brian's solution is nicer, but this still works!
