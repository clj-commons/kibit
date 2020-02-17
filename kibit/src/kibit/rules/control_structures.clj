(ns kibit.rules.control-structures
  (:use [kibit.rules.util :only [defrules]]))

(defrules rules
  {:rule [(if ?x ?y nil) (when ?x ?y)]}
  {:rule [(if ?x nil ?y) (when-not ?x ?y)]}
  {:rule [(if ?x (do . ?y)) (when ?x . ?y)]}
  {:rule [(if (not ?x) ?y ?z) (if-not ?x ?y ?z)]}
  {:rule [(if ?x ?x ?y) (or ?x ?y)]}
  {:rule [(when (not ?x) . ?y) (when-not ?x . ?y)]}
  {:rule [(do ?x) ?x]}
  {:rule [(if-let ?binding ?expr nil) (when-let ?binding ?expr)]}
  {:rule [(when ?x (do . ?y)) (when ?x . ?y)]}
  {:rule [(when-not ?x (do . ?y)) (when-not ?x . ?y)]}
  {:rule [(if-not ?x (do . ?y)) (when-not ?x . ?y)]}
  {:rule [(if-not (not ?x) ?y ?z) (if ?x ?y ?z)]}
  {:rule [(when-not (not ?x) . ?y) (when ?x . ?y)]}

  ;; suggest `while` for bindingless loop-recur
  {:rule [(loop [] (when ?test . ?exprs (recur)))
          (while ?test . ?exprs)]}
  {:rule [(let ?binding (do . ?exprs)) (let ?binding . ?exprs)]}
  {:rule [(loop ?binding (do . ?exprs)) (loop ?binding . ?exprs)]})

(comment
  (when (not (pred? x y)) (f x y))

  (if (all-streams-complete?)
    (do
      (deliver @all-clear :true)
      (info "All streams complete.")))

  (if (pred? x)
    (do (action a)
        (action b)
        (if-let [x (f a b c)
                 y (g a b c)
                 z (h a b c)]
          (do (+ 1 0)
              (= 1 1)
              (< 1 0)))
        (action d)
        (action f))
    nil))
