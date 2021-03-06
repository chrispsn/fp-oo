(ns sources.t-generic
  (:use midje.sweet))

(load-file "sources/generic.clj")

(defn ship [name] (with-meta {:name name} {:type ::ship}))
(defn asteroid [name] (with-meta {:name name} {:type ::asteroid}))


(defgeneric collide [thing1 thing2]
  [(type thing1) (type thing2)])

(defspecialized collide [::ship ::ship]
  [& args]
  "ships")

(fact "defgeneric and defspecialized are synonyms"
  (collide (ship "Space Beagle") (ship  "Rim Griffon")) => "ships")


(defgeneric collide [thing1 thing2]
  (sort [(type thing1) (type thing2)]))

(fact "old specializations still work"
  (collide (ship "Space Beagle") (ship  "Rim Griffon")) => "ships")


(defspecialized collide [::asteroid ::ship]
  [& args]
  "mix")

(fact "defgeneric redefinitions work"
  (collide (ship "Space Beagle") (asteroid  "Ceres")) => "mix")
(fact "old specializations still work"
  (collide (ship "Space Beagle") (ship  "Rim Griffon")) => "ships")

;; Another generic function does no harm.

(defgeneric pcount [number] number)

(defspecialized pcount 1 [number] "one")
(defspecialized pcount 2 [number] "two")
(defspecialized pcount :default [number] number)

(fact "new stuff works"
  (pcount 1) => "one"
  (pcount 2) => "two"
  (pcount 3) => 3)

(fact "old stuff still works"
  (collide (ship "Space Beagle") (asteroid  "Ceres")) => "mix"
  (collide (ship "Space Beagle") (ship  "Rim Griffon")) => "ships")


;;; Can redefine specialized function

(defspecialized collide [::asteroid ::ship]
  [& args]
  "new mix")

(fact "old stuff still works"
  (collide (ship "Space Beagle") (asteroid  "Ceres")) => "new mix"
  (collide (ship "Space Beagle") (ship  "Rim Griffon")) => "ships")

;;; Can find dispatch value

(fact
  (dispatch-value 'collide (ship "Space Beagle") (ship  "Rim Griffon")) => [::ship ::ship]
  (dispatch-value 'pcount 3) => 3)

;;; Can erase generic function

(forget 'collide)
(fact
  (resolve 'collide) => nil
  (get @dispatch-functions 'collide) => nil
  (get @specializations 'collide) => nil)

;;; The default generic function uses the type of the first element.

(derive ::hash-map ::abstract-map)


(defgeneric to-string)
(defspecialized to-string ::abstract-map [this] "a map of some sort")

(defgeneric get-element)
(defspecialized get-element ::abstract-map [this key] (get this key))


(fact
  (to-string (with-meta {} {:type ::hash-map})) => "a map of some sort"
  (get-element (with-meta {:a 1} {:type ::hash-map}) :a) => 1)




(defgeneric true-false odd?)
(defspecialized true-false true [_] "odd")
(defspecialized true-false false [_] "even")

(fact "function names can be used"
  (true-false 1) => "odd"
  (true-false 2) => "even")
