[![Build Status](https://travis-ci.org/jonase/kibit.svg?branch=master)](https://travis-ci.org/jonase/kibit)
[![Dependencies Status](http://jarkeeper.com/jonase/kibit/status.svg)](http://jarkeeper.com/jonase/kibit)

# kibit

*There's a function for that!*

`kibit` is a static code analyzer for Clojure, ClojureScript, [cljx](https://github.com/lynaghk/cljx)
 and other Clojure variants. It uses [`core.logic`](https://github.com/clojure/core.logic)
  to search for patterns of code that could be rewritten with a more idiomatic function
or macro. For example if kibit finds the code

```clojure
(if (some test)
  (some action)
  nil)
```

it will suggest using `when` instead:

```clojure
(when (some test)
  (some action))
```

## Usage

Add `[lein-kibit "0.0.8"]` to your `:plugins` vector in your `:user`
profile. Then you can run

    $ lein kibit

to analyze a Leiningen project's namespaces. Kibit will automatically pick up source paths from your project.clj from the following keyseqs: [:source-paths], [:cljsbuild :builds], and [:cljx :builds]. You can also run Kibit manually on individual files or folders (even if there is no Leiningen `project.clj`) by running:

    $ lein kibit path/to/some/file.clj #or
    $ lein kibit path/to/src/ #or
    $ lein kibit path/to/src/clj/ path/to/src/cljs/util.cljs some/combo/of/files/and/folders.cljx


If you want to know how the Kibit rule system works there are some slides available at [http://jonase.github.io/kibit-demo/](http://jonase.github.io/kibit-demo/).

## Exit codes

If `lein kibit` returns any suggestions to forms then it's exit code will be 1. Otherwise it will exit 0. This can be useful to add in a build step for automated testing.


    $lein kibit
    ... suggestions follow

    $echo $?
    1

## Automatically rerunning when files change

You can use [lein-auto](https://github.com/weavejester/lein-auto) to run kibit automatically when files change. Visit
lein-auto's README for installation instructions. Note that this will run kibit over all of your files, not just the
ones that have changed.

    $lein auto kibit
    auto> Files changed: project.clj, [...]
    auto> Running: lein kibit
    ... suggestions follow
    auto> Failed.
    auto> Files changed: test/my/test/misc.clj
    auto> Running: lein kibit
    ... suggestions follow
    auto> Failed.

## Reporters

Kibit comes with two reporters, the default plaintext reporter, and a GitHub Flavoured Markdown reporter. To specify a reporter, use the `-r` or `--reporter` commandline argument. For example:

    lein kibit --reporter markdown
    ----
    ##### `test/project/core.clj:31`
    Consider using:
    ```clojure
      (when true (println "hi"))
    ```
    instead of:
    ```clojure
      (if true (do (println "hi")))
    ```

    ----
    ##### `test/project/core.clj:32`
    Consider using:
    ```clojure
      (println "hi")
    ```
    instead of:
    ```clojure
      (do (println "hi"))
    ```

which renders to:

----
##### `test/project/core.clj:31`
Consider using:
```clojure
  (when true (println "hi"))
```
instead of:
```clojure
  (if true (do (println "hi")))
```
...
----

### Usage from inside Emacs

If you use Emacs for hacking Clojure, here's a way to use kibit from
inside Emacs with all the fanciness you are used to from `M-x compile`.
Put the following into your `~/.emacs`:

```clojure
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
therefore easily invalidate a rule. Also, kibit will not know if the
symbol `+` in the form `(+ x 1)` actually refers to a local or to a
function in a namespace other than `clojure.core`. Expect
some false positives.

## Contributing

It is very easy to write new patterns for `kibit`. Take a look at
[`control-structures.clj`](https://github.com/jonase/kibit/blob/master/src/kibit/rules/control_structures.clj)
to see how new patterns are created. If you know of a recurring
pattern of code that can be simplified, please consider sending me a
pull request.

Bugs can be reported using the GitHub [issue tracker](https://github.com/jonase/kibit/issues/).

## Contributors

Thanks to all who have [contributed](https://github.com/jonase/kibit/graphs/contributors) to kibit!

## TODO

* Leiningen project.clj setting for rule exclusion
* Leiningen project.clj setting for a directory of rules to include

## License

Copyright © 2012 Jonas Enlund

Distributed under the Eclipse Public License, the same as Clojure.
