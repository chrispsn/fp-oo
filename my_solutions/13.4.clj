; For checking...

(def eager?
     (fn [filter-function]
       (try
         (not (first (filter-function (fn [elt]
                                        (if (= elt 9999)
                                          (throw (Error.)))
                                        true)
                                      (range 10000))))
         (catch Error e
           true))))


; Exercise 1: map

; given code

(def rrange
    (fn [first past-end]
        (new clojure.lang.LazySeq
            (fn []
                (if (= first past-end)
                    nil
                    (cons first
                        (rrange (inc first) past-end)
                    )
                )
            )
        )
    )
)

; my code

(defn mmap [function args]
    (new clojure.lang.LazySeq (fn []
        (if (empty? args)
            nil
            (cons 
                (function (first args))
                (mmap function (rest args)) 
            )
        )
    ))
)

(println (mmap inc [0 1 2]))
(println (eager? mmap))


; Exercise 2: filter

(defn ffilter [predicate args]
    (new clojure.lang.LazySeq (fn []
        (cond   (empty? args)
                nil

                ; I realise that Brian's answer probs doesn't duplicate
                ; (filter predicate (rest args)) - but I prefer this
                ; as it actually simplifies the code for understanding purposes.

                (predicate (first args))
                (cons (first args) (ffilter predicate (rest args)))

                :else
                (ffilter predicate (rest args))
        )
    ))
)


(println (ffilter odd? [0 1 2 3]))
(println (eager? ffilter))


; Exercise 3: eager filter

(defn ffilter [predicate args]
    (cond   (empty? args)
            nil

            (predicate (first args))
            (cons (first args) (ffilter predicate (rest args)))

            :else
            (ffilter predicate (rest args))
    )
)

(println (ffilter odd? [0 1 2 3 4]))
(println (eager? ffilter))
