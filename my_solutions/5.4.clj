;; Exercise 1

(def apply-message-to
    (fn [class instance message args]
        (let [method (message (:__instance_methods__ class))]
            (apply method instance args)
        )
    )
)

(def a
     (fn [class & args]
       (let [seeded {:__class_symbol__ (:__own_symbol__ class)}
             constructor :add-instance-values]
         (apply-message-to class seeded constructor args)
        )
    )
)

(def send-to
     (fn [instance message & args]
       (let [class (eval (:__class_symbol__ instance))]
         (apply-message-to class instance message args)
        )
    )
)

;; Exercise 2

(def Point
{
  :__own_symbol__ 'Point
  :__instance_methods__
  {
    :add-instance-values (fn [this x y]
                           (assoc this :x x :y y))
    :class-name :__class_symbol__
    :class (fn [this] eval (eval (send-to this :class-name)))
    :shift (fn [this xinc yinc]
             (a Point (+ (:x this) xinc)
                      (+ (:y this) yinc)))
    :add (fn [this other]
           (send-to this :shift (:x other)
                                (:y other)))
   }
 })


;; For exercise 4
(def Holder  
{
  :__own_symbol__ 'Holder
  :__instance_methods__
  {
    :add-instance-values (fn [this held]
                           (assoc this :held held))
  }
})


;; Exercise 4

(def apply-message-to
    (fn [class instance message args]
        (let [method (message (:__instance_methods__ class))]
            (apply (or method message) instance args)
        )
    )
)


;; Exercise 5
(send-to (a Point 1 2) :some-unknown-message)
;; => nil
