; starter code

(use 'patterned.sweet)

(defpatterned count-sequence
  [so-far [           ] ] so-far
  [so-far [head & tail] ] (count-sequence (inc so-far) tail))


; my solution

; Exercise 1

(defpatterned count-sequence
  [so-far [           ] ] so-far
  [       [     & tail] ] (count-sequence 0            tail)
  [so-far [head & tail] ] (count-sequence (inc so-far) tail))

; test
(println (count-sequence '(:a :b :c)))  ; 3


; Exercise 2

; my solution

(defpatterned pattern-reduce 
  [function so-far [           ] ] so-far
  [function so-far [   single  ] ] (function so-far single)
  [function so-far [head & tail] ] (pattern-reduce function (function so-far head) tail)
)

; test

(println (pattern-reduce (fn [so-far elt] (cons elt so-far))
                            []
                            [:a :b :c])) ; output: [:c :b :a]
