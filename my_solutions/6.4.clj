;; Exercise 1

(def factorial
    (fn [n]
        (if (= 1 n)
            1
            (* n (factorial (dec n)))
        )
    )
)

(prn 'Exercise1)
(prn (factorial 6))


;; Exercise 2

(def factorial
    (fn [n so-far]
        (if (= n 1)
            so-far
            (factorial (dec n) (* so-far n))
        )
    )
)

(prn 'Exercise2)
(prn (factorial 6 1))


;; Exercise 3

(def seq-add
    (fn [seq result]
        (if (empty? seq)
            result
            (seq-add (rest seq) (+ result (first seq)))
        )
    )
)

(prn 'Exercise3)
(prn (seq-add [1 2 3 4] 0))


;; Exercise 4
(def seq-op
    (fn [op seq result]
        (if (empty? seq)
            result
            (seq-op op (rest seq) (apply op [result (first seq)]))
        )
    )
)

(prn 'Exercise4)
(prn (seq-op + [1 2 3 4] 0))
(prn (seq-op * [1 2 3 4] 1))


;; Exercise 5
(def my-reduce
    (fn [combiner a-list so-far]
        (if (empty? a-list)
            so-far
            (my-reduce combiner (rest a-list) (apply combiner [so-far (first a-list)]))
        )
    )
)

(def silly-map
    (fn [a-map key]
        (assoc a-map key 0)
    )
)

(def better-map
    (fn [a-map key]
        (assoc a-map key (count a-map))
    )
)

(prn 'Exercise5)
(prn (my-reduce silly-map [:a :b :c] {}))
(prn (my-reduce better-map [:a :b :c] {}))
