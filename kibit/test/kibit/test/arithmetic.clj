(ns kibit.test.arithmetic
  (:require [kibit.check :as kibit])
  (:use [clojure.test]))

(deftest arithmetic-are
  (are [expected-alt-form test-form]
       (= expected-alt-form (:alt (kibit/check-expr test-form)))
    '(inc num) '(+ num 1)
    '(inc num) '(+ 1 num)
    '(dec num) '(- num 1)
    '(* x y z) '(* x (* y z))
    '(+ x y z) '(+ x (+ y z))

    ;;hypot
    '(Math/hypot x y) '(Math/sqrt (+ (Math/pow x 2) (Math/pow y 2)))

    ;;special exponential form
    '(Math/expm1 x) '(- (Math/exp x) 1)
    '(Math/expm1 x) '(dec (Math/exp x))

    ;;not that ugly rounding trick, thank you
    '(Math/round x) '(long (+ x 0.5))

    ;;trivial identities
    'x '(+ x 0)
    'x '(- x 0)
    'x '(* x 1)
    'x '(/ x 1)
    '0 '(* x 0)))
