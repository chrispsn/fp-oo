
(use 'clojure.algo.monads)

(def function-monad
     (monad [m-result
             (fn [binding-value]
               (fn [] binding-value))

             m-bind
             (fn [monadic-value continuation]
               (let [binding-value (monadic-value)]
                 (continuation binding-value)))]))

(def calculation
     (with-monad function-monad
       (let [frozen-step m-result]
         (domonad [a (frozen-step 8)
                   b (frozen-step (+ a 88))]
              (+ a b)))))


;;;; Charging monad

(def charging-monad
     (monad [m-result 
             (fn [result]
               (fn [charge]
                 {:charge charge, :result result}))

             m-bind
             (fn [monadic-value continuation]
               (fn [charge]
                 (let [enclosed-map (monadic-value charge)
                       binding-value (:result enclosed-map)]
                   ( (continuation binding-value)
                     (inc charge)))))]))

(def run-and-charge
     (with-monad charging-monad
       (let [frozen-step m-result]
         (domonad [a (frozen-step 8)
                   b (frozen-step (+ a 88))]
                  (+ a b)))))

; Prediction: we should be able to pass in anything, because
; that charge number really isn't used in anything - all
; the continuation uses is the result.
; expected: charge 6, result 104

; yep - it's the same.
;(println (run-and-charge 4))


;;;; Exercise 2

;(use '[clojure.pprint :only [cl-format]])
;
;(def verbose-charging-monad
;     (monad [m-result 
;             (fn [result]
;               (cl-format true "Freezing ~A.~%" result)
;               (fn [charge]
;                 (cl-format true "Unfrozen calculation gets charge ~A.~%" charge)
;                 (cl-format true "... The frozen calculation result was ~A.~%" result)
;                 {:charge charge, :result result}))
;
;             m-bind
;             (fn [monadic-value continuation]
;               (cl-format true "Making a decision.~%")
;               (fn [charge]
;                 (let [enclosed-map (monadic-value charge)
;                       binding-value (:result enclosed-map)]
;                   (cl-format true "Calling continuation with ~A~%" binding-value)
;                   (cl-format true "... The charge to increment is ~A~%", charge)
;                   ( (continuation binding-value)
;                     (inc charge)))))]))
;
;(println "==========")
;(println "Defining run-and-charge.")
;         
;(def run-and-charge-and-speak
;     (with-monad verbose-charging-monad
;       (let [frozen-step m-result]
;         (domonad [a (frozen-step 8)
;                   b (frozen-step (+ a 88))]
;                  (+ a b)))))
;
;(println "-----------")
;(println "Running run-and-charge.")
;;(println (run-and-charge-and-speak 4))


; wtf why is a decision made before the function's even called
; also it's freezing the value straight away
; ohhhhhhhh
; I think it's because I'm assuming that things defined with def
; aren't evaluated until they're called - but I should remember
; that that's only true for functions.
; So first thing that happens is m-result is called with 8 as an argument,
; which returns a monadic value (a fn that takes a charge and outputs a certain map).
; (This is why 'Freezing 8' appears.)
; M-bind is then called, taking both (1) the monadic value produced by (frozen-step 8),
; and (2) the continuation. (That's why it's making a decision.)
; BUT the function returned by m-bind isn't called until we call it with a starter charge.
; That function is the output of the whole 'with-monad' thing,
; and it's also what's called 'run-and-charge-and-speak'.
; Everything else is 'frozen' in the continuation until we call run-and-charge-and-speak.



;;;; Exercise 3

;(use '[clojure.pprint :only [cl-format]])
;
;(def verbose-charging-monad
;     (monad [m-result 
;             (fn [result]
;               (cl-format true "Freezing ~A.~%" result)
;               (fn [charge]
;                 (cl-format true "Unfrozen calculation gets charge ~A.~%" charge)
;                 (cl-format true "... The frozen calculation result was ~A.~%" result)
;                 {:charge (inc charge), :result result}))
;
;             m-bind
;             (fn [monadic-value continuation]
;               (cl-format true "Making a decision.~%")
;               (fn [charge]
;                 (let [enclosed-map (monadic-value charge)
;                       binding-value (:result enclosed-map)]
;                   (cl-format true "Calling continuation with ~A~%" binding-value)
;                   (cl-format true "... The charge to increment is ~A~%", charge)
;                   ( (continuation binding-value)
;                     charge))))]))
;
;(println "==========")
;(println "Defining run-and-charge.")
;         
;(def run-and-charge-and-speak
;     (with-monad verbose-charging-monad
;       (let [frozen-step m-result]
;         (domonad [a (frozen-step 8)
;                   b (frozen-step (+ a 88))]
;                  (+ a b)))))
;
;(println "-----------")
;(println "Running run-and-charge.")
;(println (run-and-charge-and-speak 4))


; We get a charge of 5 - ie it's only incremented once.
; That's because of a reason covered in exercise 1 - that is,
; the charge value of m-result doesn't matter until the 'bottom layer'
; of the computation, where it's the thing which is returned.
; What matters is the charge value that's passed as an argument to m-bind.
; The second time m-result is called (when the first 'continuation' is called),
; it's using the same value for 'charge' that we originally defined,
; passed in from the first m-bind. But again, its charge is thrown away.
; It DOES matter when m-result is called on (+ a b)'s return value.
; That's the final return value of the function produced by the continuation.

; I admit to not being able to fit exactly what's going on 100% into my head.
; Could probably logic it out. Though I guess it doesn't really matter -
; based on the rules, I know that the return result of ((continuation binding-value) charge)
; will be a map, and that map will be one produced intact from m-result.
; It'll therefore be incremented.


; intermission: deconstructing p 247's "calculation-with-initial-state"

; assign-state gets called with an argument (new-state) of 88.
; that produces the monadic value, being a function with one input (state) and outputs a specific map.
; It hands this off to m-bind, which also gets the continuation.
; m-bind then outputs a function which takes *a* state as input, and then:
;   (1) calls the continuation with the old state, returning a monadic value, and then 
;   (2) calls the monadic value returned as the output of (1) with new-state as the argument.

; this is what we call eventually.

; so what happens in that continuation?:
; the second m-bind takes as INPUTs:
; (a) the continuation and
; (b) the output of (get-state), which is a function that takes *some* state as an input and outputs
;     a map which has both :result and :state as that state.
; note it does use (b) in its processing of what it eventually outputs, _but it hasn't done that yet_.
; it OUTPUTs:
; <monadifier takes [result], which is just (str ...) which knows its original-state and state
; since original state was already compuedthey were computed above, and outputs the monadic value with result as the (str ...) and state as the state;
; this monadic value is returned by <a function which takes in <old-state>. <== UNSURE TODO
; This function is the continuation for the second m-bind's output.> <== UNSURE TODO
; So the output of the function returned by the second m-bind is (<function referred to above> <new state>),
; ie the same map shape we've been dealing with all along.

; SO. The point is that the function returned by m-bind is called with new-state as its argument.
; So everything flows through - eg the (b) referred to above has both result and state as the value of new-state.

; Phew!


;;;; Exercises 4 and 5

;;; State monad

(def state-monad
     (monad [m-result 
             (fn [result]
               (fn [state]
                 {:state state, :result result}))

             m-bind
             (fn [monadic-value continuation]
               (fn [state]
                 (let [enclosed-map (monadic-value state)
                       binding-value (:result enclosed-map)
                       new-state (:state enclosed-map)]
                   (  (continuation binding-value) new-state))))]))

(def get-state
     (fn []
       (fn [state]
         {:state state, :result state})))


(def assign-state
     (fn [new-state]
       (fn [state]
         {:state new-state, :result state})))


(def calculation-with-initial-state
     (with-monad state-monad
       (domonad [original-state (assign-state 88)
                 state (get-state)]
            (str "original state " original-state " was set to " state))))

 (def mixer
      (with-monad state-monad
        (let [frozen-step m-result]
          (domonad [original (get-state)
                    a (frozen-step (+ original 88))
                    b (frozen-step (* a 2))
                    _ (assign-state b)]
                   [original a b]))))



; my answers
; Exercise 4
(defn transform-state [function]
    (fn [state]
        {:state (function state) :result state}
    )
)

; test
(def transform-state-example
    (domonad [b (transform-state inc)]
        b)
)

; TODO wtf - where's with-monad to say we're using the state monad?
(println (transform-state-example 1))


; Exercise 5

(def get-state
     (fn [variable]
       (fn [state]
         {:state state, :result (variable state)})))


(def assign-state
     (fn [variable new-state]
       (fn [state]
         {:state (assoc state variable new-state), :result (variable state)})))

(defn transform-state [variable function]
    (fn [state]
        (let [new-state 
             (assoc state variable (function (variable state)))]
            {:state new-state :result (variable state)}
        )
    )
)

(def map-state-example
    (with-monad state-monad
    (domonad [a (get-state :a)
                old-b (assign-state :b 3)
                old-c (transform-state :c inc)]
        [a old-b old-c])))

(println (map-state-example {:a 1 :b 2 :c 3}))


; I looked at the answers to see what functions I had to change, but didn't look at their bodies - 
; I don't understand how we could fix this without needing to change the state-monad...
; Oh, of course. The thing bound to (eg) a is :result, but the :state as altered by (get-state :a) or equivalent
; is always passed to the relevant m-bind.
