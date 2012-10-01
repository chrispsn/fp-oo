(use 'clojure.set) ;; Not actually used here, but will be in exercises.


; Courses is a list of courses (maps).
; Registrants-courses is a list of courses that the registrant is in.
(def answer-annotations
     (fn [courses registrants-courses]
       (let [checking-set (set registrants-courses)]
         (map (fn [course]
                (assoc course
                       :spaces-left (- (:limit course)
                                       (:registered course))
                       :already-in? (contains? checking-set
                                               (:course-name course))))
              courses))))

; Adds info re: whether the courses are empty or full.
(def domain-annotations
     (fn [courses]
       (map (fn [course]
              (assoc course
                :empty? (zero? (:registered course))
                :full? (zero? (:spaces-left course))))
            courses)))

; Tells us whether a course is unavailable, either because
; (a) it's full, or (b) it's empty, but no more instructors are available.
(def note-unavailability
     (fn [courses instructor-count]
       (let [out-of-instructors?
             (= instructor-count
                (count (filter (fn [course] (not (:empty? course)))
                               courses)))]
         (map (fn [course]
                (assoc course
                       :unavailable? (or (:full? course)
                                         (and out-of-instructors?
                                              (:empty? course)))))
              courses))))

;; Removed previous iterations of annotate
; This just adds the annotations produced by the three functions above
; to the course maps, in sequence.
(def annotate
     (fn [courses registrants-courses instructor-count]
       (-> courses
           (answer-annotations registrants-courses)
           domain-annotations
           (note-unavailability instructor-count))))


; Produces two sequences from an input sequence,
; one for which the predicate is true for all in the output,
; and another in which the predicate is not true for any in the output.
(def separate
     (fn [pred sequence]
       [(filter pred sequence) (remove pred sequence)]))


; Only show those courses which either (a) the student is already in
; or (b) are not unavailable.
(def visible-courses
     (fn [courses]
       (let [[guaranteed possibles] (separate :already-in? courses)]
         (concat guaranteed (remove :unavailable? possibles)))))


; Only keep certain keys in the course maps.
(def final-shape
     (fn [courses]
       (let [desired-keys [:course-name :morning? :registered :spaces-left :already-in?]]
         (map (fn [course]
                (select-keys course desired-keys))
              courses))))


; Takes courses in a list of those offered in a half-day.
; Only keeps those to show. Sorts them in alphabetical order.
(def half-day-solution
     (fn [courses registrants-courses instructor-count]
       (-> courses
           (annotate registrants-courses instructor-count)
           visible-courses
           ((fn [courses] (sort-by :course-name courses)))
           final-shape)))


; A course can appear twice in the list of courses
; - once in the morning and once in the afternoon.
(def solution
     (fn [courses registrants-courses instructor-count]
       (map (fn [courses]
              (half-day-solution courses registrants-courses instructor-count))
            (separate :morning? courses))))


;  My solutions
; Exercise 1
; We want to make courses in the afternoon unavailable to managers.
; We can therefore add to note-unavailability, making it add an
; additional check for whether the employee is a manager.
; The data about the person indicates that perhaps it's better
; we make the person a map, which has :manager? and :courses keys.
; So anything which has to deal with registrants-courses would now need
; to deal with registrant and get the courses value out using the :courses key.

; Alternatively, we could add a simple boolean check for whether the employee
; is a manager in the note-unavailability function. Although we're starting to
; not include related info in the same structure.

; Exercise 2
; This further implies that turning registrants-courses into a value within
; a registrant map is the right way to go, because we can just make
; the courses the employee has already done part of the registrant map,
; then add a check into note-unavailability to see whether, for each course,
; the employee's list of courses already completed doesn't contain at least one of the 
; course's prerequisites (using 
; (difference <set of course pre-requisites> <set of courses completed by the registrant>)).
