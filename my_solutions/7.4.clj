(declare class-from-instance send-to send-super apply-message-to)
;; This imports a function from another namespace. (Think package or module.)
(use '[clojure.pprint :only [cl-format]])


(def MetaAnything
{
  :__own_symbol__ 'MetaAnything
  :__instance_methods__
  {
   :new
   (fn [class & args]
     (let [seeded {:__class_symbol__ (:__own_symbol__ class)}]
       (apply-message-to class seeded :add-instance-values args)))
   }
 })

(def Anything
{
  :__own_symbol__ 'Anything
  :__class_symbol__ 'MetaAnything
  :__instance_methods__
  {
    :add-instance-values identity
    :method-missing
    (fn [this message args]
      (throw (Error. (cl-format nil "A ~A does not accept the message ~A."
                                (send-to this :class-name)
                                message))))
    :to-string (fn [this] (str this))
    :class-name :__class_symbol__    
    :class (fn [this] (class-from-instance this))
   }
 })

(def MetaPoint
{
  :__own_symbol__ 'MetaPoint
  :__superclass_symbol__ 'MetaAnything
  :__instance_methods__
  {
   :origin (fn [class] (send-to class :new 0 0))
   }
 })


(def Point
{
  :__own_symbol__ 'Point
  :__class_symbol__ 'MetaPoint
  :__superclass_symbol__ 'Anything
  :__instance_methods__
  {
    :x :x
    :y :y 
    :add-instance-values (fn [this x y]
                           (assoc this :x x :y y))
    :shift (fn [this xinc yinc]
             (send-to Point :new (+ (:x this) xinc)
                                 (+ (:y this) yinc)))
    :add (fn [this other]
           (send-to this :shift (:x other)
                                (:y other)))
    :to-string
    (fn [this]
      (cl-format nil "A ~A like this: ~A"
                 (send-to this :class-name)
                 (send-super this :to-string)))
   }
 })



;;; Here are methods that take a class-symbol or instance containing one and follow it somewhere. 

(def class-symbol-above
     (fn [class-symbol]
       (assert (symbol? class-symbol))
       (:__superclass_symbol__ (eval class-symbol))))

(def class-instance-methods
     (fn [class-symbol]
       (assert (symbol? class-symbol))
       (:__instance_methods__ (eval class-symbol))))

(def class-from-instance
     (fn [instance]
       (assert (map? instance))
       (eval (:__class_symbol__ instance))))

(def superclass-from-instance
     (fn [instance]
       (assert (map? instance))
       (eval (:__superclass_symbol__ (class-from-instance instance)))))

;; Core dispatch function

(def lineage-1
     (fn [class-symbol so-far]
       (if (nil? class-symbol)
         so-far
         (lineage-1 (class-symbol-above class-symbol)
                    (cons class-symbol so-far)))))
(def lineage
     (fn [class-symbol]
       (lineage-1 class-symbol [])))

(def method-cache
     (fn [class]
       (let [class-symbol (:__own_symbol__ class)
             method-maps (map class-instance-methods
                              (lineage class-symbol))]
         (apply merge method-maps))))

(def apply-message-to
     (fn [class instance message args]
       (let [method (message (method-cache class))]
         (if method
           (apply method instance args)
           (send-to instance :method-missing message args)))))

;;; The public interface

(def send-to
     (fn [instance message & args]
       (apply-message-to (class-from-instance instance)
                         instance message args)))

(def send-super
     (fn [instance message & args]
       (apply-message-to (superclass-from-instance instance)
                         instance message args)))
