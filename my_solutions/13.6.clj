;;; Starter source

(def prompt-and-read
     (fn []
       (print "> ")
       (.flush *out*)
       (.readLine
        (new java.io.BufferedReader *in*))))




;;; my answers

; Exercise 1

; I'm guessing it was because filter produced a lazy sequence which singles
; now points to.

; Exercise 2

(def inputs (repeatedly prompt-and-read))  
; haha - turns out my error was brackets around prompt-and-read

(defn y-or-n? [line] (or 
    (.startsWith line "y")
    (.startsWith line "n")
))

(def ys-and-ns (filter y-or-n? inputs)) 

;(println (take 2 ys-and-ns))

; Exercise 3

;;; starter code

(def counted-sum
     (fn [number-count numbers]
       (apply +
              (take number-count
                    numbers))))

(def number-string?
     (fn [string]
       (try
         (Integer/parseInt string)
         true
       (catch NumberFormatException ignored
           false))))

(def to-integer
     (fn [string]
       (Integer/parseInt string)))

; my code

; ORIGINAL
(def counted-sum 
    (let [number-count (to-integer (first (filter number-string? (repeatedly prompt-and-read))))]
        (reduce + 
            (take number-count 
                (map to-integer 
                    (filter number-string? (repeatedly prompt-and-read))
                )
            )
        )
    )
)

; (println (counted-sum)) ; broken because counted-sum doesn't need to be evaluated like a function
(println counted-sum)

; REVISED - no idea why this works but the above doesn't... oohhhhh, it's because
; println tries to treat what's produced as a function because it's first in the list.
(defn counted-sum []
    (let [number-count (to-integer (first (filter number-string? (repeatedly prompt-and-read))))]
        (reduce + 
            (take number-count 
                (map to-integer 
                    (filter number-string? (repeatedly prompt-and-read))
                )
            )
        )
    )
)

(println (counted-sum))

; otherwise, I like what Brian did using first and rest to avoid needing to type
(first (filter number-string? ...)) twice.
