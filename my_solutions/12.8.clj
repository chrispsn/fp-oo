(require '[clojure.zip :as zip])


;; This is a handy function for inserting into a flow:
;; (-> zipper zlog zip/right zlog...)
(def zlog
     (fn [z] (println "LOG:" (pr-str (zip/node z))) z))

;; This prints the tree above the current node.
(def zuplog
     (fn [z] (zlog (zip/up z)) z))


;; For the second set of exercises

; Exercise 3

(def helper (fn [zipper]
    (cond (zip/end? zipper)
          zipper
          
          (= (zip/node zipper) '+)
          (-> zipper
              (zip/replace 'PLUS)
              zip/next
              helper)

          (and (zip/branch? zipper)
              (= (-> zipper zip/down zip/node) '-))
          (-> zipper
              (zip/append-child 55555)
              zip/next
              helper)

          (and (zip/branch? zipper)
              (= (-> zipper zip/down zip/node) '*))
          (-> zipper
              (zip/replace '(/ 1 (+ 3 (- 0 9999))))
              zip/next
              helper)

          (= (zip/node zipper) '/)
          (-> zipper
              zip/right
              zip/remove
              zip/right
              zip/remove
              (zip/insert-right (-> zipper zip/right zip/node))
              (zip/insert-right (-> zipper zip/right zip/right zip/node))
              zip/next
              helper)

          :else 
          (-> zipper zip/next helper)
    )
))


(def tumult
    (fn [form]
    (-> form zip/seq-zip helper zip/root)
    )
)

(println (tumult '(- 3 (+ 6 (+ 3 4) (* 2 1) (/ 8 3)))))

; Exercise 4
; note: my solution produces different output from Brian's because he thought replaced nodes
; should be processed again (or, potentially, that the check for and replacement of '-' should
; occur at node's parent, rather than the node itself).

(def helper (fn [zipper]
    (cond (zip/end? zipper)
          zipper
          
          (= (zip/node zipper) '+)
          (-> zipper
              (zip/replace 'PLUS)
              zip/next
              helper)

          (and (zip/branch? zipper)
              (= (-> zipper zip/down zip/node) '-))
          (-> zipper
              (zip/append-child 55555)
              zip/next
              helper)

          (= (-> zipper zip/node) '*)
          (-> zipper
              (zip/replace '-)
              zip/next
              helper)

          (= (zip/node zipper) '/)
          (-> zipper
              zip/right
              zip/remove
              zip/right
              zip/remove
              (zip/insert-right (-> zipper zip/right zip/node))
              (zip/insert-right (-> zipper zip/right zip/right zip/node))
              zip/next
              helper)

          :else 
          (-> zipper zip/next helper)
    )
))
(println (tumult '(* 1 2)))
(println (tumult '(- 3 (+ 6 (+ 3 4) (* 2 1) (/ 8 3)))))


; Exercise 5

(def transform (fn [zipper]
    (cond (zip/end? zipper)
          zipper

          (= (zip/node zipper) '=>)
          ; note: could define the replacement node here as in Brian's soln
          (-> 
            zipper

            ; Get rid of left node
            zip/left
            zip/remove
            zip/next ; Because remove goes to the previous node,
                     ; we get back to the main one by using next

            ; Get rid of right node
            zip/right
            zip/remove

            ; Put in our new branch
            (zip/replace '(=>))
            (zip/insert-child (-> zipper zip/left zip/node))
            (zip/insert-child 'expect)
            (zip/append-child (-> zipper zip/right zip/node))

            ; Move on; note:
            ; - can't use next because that will go down into this node)
            ; - can't use 'right' because there might not be one -> produces nil
            ; (this was a hard problem to solve)
            zip/down
            zip/rightmost
            zip/next
            transform
          )

          (or (= (zip/node zipper) 'facts) (= (zip/node zipper) 'fact))
          (-> zipper (zip/replace 'do) zip/next transform)
          
          :else
          (-> zipper zip/next transform)
    )
))

(def transform-pipeline
    (fn [form]
        (-> form zip/seq-zip transform zip/root)
    )
)

(def test-form 
    '(facts 
        (+ 1 2) => 3 
        3 => odd?)
)
(println (transform-pipeline test-form))


; Exercise 6

; Note: only leaves have values (p 156). Empty parentheses don't count.
; Postscript: apparently they do, according to Brian's solution?

(def backtrack-to-leaf (fn [zipper]
    (cond   (zip/branch? zipper)
            (-> zipper zip/prev backtrack-to-leaf)

            :else
            zipper
    )
))

(def skip-to-rightmost-leaf (fn [zipper]
    (-> zipper
        zip/rightmost
        (zip/insert-right 'temp-node)
        zip/right
        zip/remove
        backtrack-to-leaf
    )
))

(def skip-to-rightmost-leaf-pipeline (fn [form]
    (-> form zip/seq-zip zip/next skip-to-rightmost-leaf)
))

(def test-form '(' (1 (2 3))))

; Setting up so we start at the quote and see the last node
(println (-> 
            test-form
            skip-to-rightmost-leaf-pipeline
            zip/node
        )
)


; Exercise 7
; I dunno. It works, but it feels kinda hacky.
(def transform (fn [zipper]
    (cond (zip/end? zipper)
          zipper

          (or (= (zip/node zipper) 'facts) (= (zip/node zipper) 'fact))
          (-> zipper (zip/replace 'do) zip/next transform)
          
          (= (zip/node zipper) 'quote)
          (->   
            zipper
            skip-to-rightmost-leaf
            zip/next
            transform
          )

          (= (zip/node zipper) '=>)
          ; note: could define the replacement node here as in Brian's soln
          (-> 
            zipper

            ; Get rid of left node
            zip/left
            zip/remove
            zip/next ; Because remove goes to the previous node,
                     ; we get back to the main one by using next

            ; Get rid of right node
            zip/right
            zip/remove

            ; Put in our new branch
            (zip/replace '(=>))
            (zip/insert-child (-> zipper zip/left zip/node))
            (zip/insert-child 'expect)
            (zip/append-child (-> zipper zip/right zip/node))

            ; Move on
            zip/down
            skip-to-rightmost-leaf
            zip/next
            transform
          )

          :else
          (-> zipper zip/next transform)
    )
))

(def transform-pipeline
    (fn [form]
        (-> form zip/seq-zip transform zip/root)
    )
)

(def test-form 
    '(fact
        (first '((+ 1 2) => 3))
        => '(+ 1 2)
    )
)
(println (transform-pipeline test-form))

(def test-form 
    '(facts 
        (+ 1 2) => 3 
        3 => odd?)
)
(println (transform-pipeline test-form))


; Exercise 8
; oh god this is horrible

(def transform (fn [zipper]
    (cond (zip/end? zipper)
          zipper

          (or (= (zip/node zipper) 'facts) (= (zip/node zipper) 'fact))
          (-> zipper (zip/replace 'do) zip/next transform)
          
          (= (zip/node zipper) 'quote)
          (->   
            zipper
            skip-to-rightmost-leaf
            zip/next
            transform
          )

          ; Fake functions
          (= (zip/node zipper) 'provided)
          (->   
            zipper
            ; We want to append this entire fake defn 
            ; node into the prev node
            zip/up
            zip/left
            (zip/append-child (-> zipper zip/up zip/node))
            ; Replace provided with fake
            zip/down
            zip/rightmost
            zip/down
            (zip/replace 'fake)
            ; Now: get out and nuke the old fake function node
            zip/rightmost
            zip/next
            zip/remove
            zip/next
            transform
          )

          (= (zip/node zipper) '=>)
          ; note: could define the replacement node here as in Brian's soln
          (-> 
            zipper

            ; Get rid of left node
            zip/left
            zip/remove
            zip/next ; Because remove goes to the previous node,
                     ; we get back to the main one by using next

            ; Get rid of right node
            zip/right
            zip/remove

            ; Put in our new branch
            (zip/replace '(=>))
            (zip/insert-child (-> zipper zip/left zip/node))
            (zip/insert-child 'expect)
            (zip/append-child (-> zipper zip/right zip/node))

            ; Move on
            zip/down
            skip-to-rightmost-leaf
            zip/next
            transform
          )

          :else
          (-> zipper zip/next transform)
    )
))

(def transform-pipeline
    (fn [form]
        (-> form zip/seq-zip transform zip/root)
    )
)

(def test-form 
    '(fact
        (first '((+ 1 2) => 3))
        => '(+ 1 2)
    )
)
(println (transform-pipeline test-form))

(def test-form 
    '(facts 
        (+ 1 2) => 3 
        3 => odd?)
)
(println (transform-pipeline test-form))

(def test-form
    '(fact
        (function-under-test 3) => -88
            (provided (subsidiary-function 3) => 88)
    )
)
(println (transform-pipeline test-form))
