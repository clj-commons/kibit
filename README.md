# kibit

*There's a function for that!*

`kibit` is a static code analyzer for Clojure which uses
[`core.logic`](https://github.com/clojure/core.logic) to search for
patterns of code for which there might exist a more idiomatic function
or macro. For example if kibit finds the code

    (if (some test)
      (some action)
      nil)

it will make the suggestion to use the `when` macro instead of `if`.

## Usage

Add `[lein-kibit "0.0.8"]` to your `:plugins` vector in your `:user`
profile. Then you can run

    $ lein kibit

to analyze your namespaces. You can analyze individual files by
running

    $ lein kibit path/to/some/file.clj

### Usage from inside Emacs

If you use Emacs for hacking Clojure, here's a way to use kibit from
inside Emacs with all the fancyness you are used from `M-x compile`.
Put the following into your `~/.emacs`:

```
;; Teach compile the syntax of the kibit output
(require 'compile)
(add-to-list 'compilation-error-regexp-alist-alist
	     '(kibit "At \\([^:]+\\):\\([[:digit:]]+\\):" 1 2 nil 0))
(add-to-list 'compilation-error-regexp-alist 'kibit)

;; A convenient command to run "lein kibit" in the project to which
;; the current emacs buffer belongs to.
(defun kibit ()
  "Run kibit on the current project.
Display the results in a hyperlinked *compilation* buffer."
  (interactive)
  (compile "lein kibit"))

(defun kibit-current-file ()
  "Run kibit on the current file.
Display the results in a hyperlinked *compilation* buffer."
  (interactive)
  (compile (concat "lein kibit " buffer-file-name)))
``` 

This will give you a new command `M-x kibit RET`, and the properly
highlighted and hyperlinked kibit output is presented in a
`*compilation*` buffer.

## Known limitations

Kibit
[reads](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/read)
source code without any macro expansion or evaluation. A macro can
therefor easily invalidate a rule. Also, kibit will not know if the
symbol `+` in the form `(+ x 1)` actually refers to a local or to a
function in a namespace other than `clojure.core`. Expect
some false positives.

## Contributing

It is very easy to write new patterns for `kibit`. Take a look at
[`control-structures.clj`](https://github.com/jonase/kibit/blob/master/src/kibit/rules/control_structures.clj)
to see how new patterns are created. If you know of a recurring
pattern of code that can be simplified, please consider sending me a
pull request.

Bugs can be reported using the github bug tracker.

## Contributors

Thanks to all who have [contributed](https://github.com/jonase/kibit/graphs/contributors) to kibit!

## TODO

* Leiningen project.clj setting for rule exclusion
* Leiningen project.clj setting for a directory of rules to include
* Analyse ClojureScript files

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.

