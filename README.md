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

Add `[lein-kibit "0.1.7"]` to your `:plugins` vector in your `:user` profile. Then you can run

    $ lein kibit

to analyze a Leiningen project's namespaces. Kibit will automatically pick up source paths from your project.clj from the following keyseqs: `[:source-paths]`, `[:cljsbuild :builds]`, and `[:cljx :builds]`. You can also run Kibit manually on individual files or folders (even if there is no Leiningen `project.clj`) by running:

    $ lein kibit path/to/some/file.clj #or
    $ lein kibit path/to/src/ #or
    $ lein kibit path/to/src/clj/ path/to/src/cljs/util.cljs some/combo/of/files/and/folders.cljx


If you want to know how the Kibit rule system works there are some slides available at [http://jonase.github.io/kibit-demo/](http://jonase.github.io/kibit-demo/).

## Exit codes

If `lein kibit` returns any suggestions to forms then its exit code will be 1. Otherwise it will exit 0. This can be useful to add in a build step for automated testing.


    $ lein kibit
    ... suggestions follow

    $ echo $?
    1

## Automatically rerunning when files change

You can use [lein-auto](https://github.com/weavejester/lein-auto) to run kibit automatically when files change. Visit
lein-auto's README for installation instructions. Note that this will run kibit over all of your files, not just the
ones that have changed.

    $ lein auto kibit
    auto> Files changed: project.clj, [...]
    auto> Running: lein kibit
    ... suggestions follow
    auto> Failed.
    auto> Files changed: test/my/test/misc.clj
    auto> Running: lein kibit
    ... suggestions follow
    auto> Failed.

## Automatically replacing suggestions in source file

You can have kibit automatically apply suggestions to your source files.

Given a file:

```clojure
(ns example)

(+ 1 a)
```

    $ lein kibit --replace

will rewrite the file as:

```clojure
(ns example)

(inc a)
```

Replacement can also be run interactively:

    $ lein kibit --replace --interactive
     Would you like to replace
       (+ 1 a)
      with
       (inc a)
     in example.clj:3? [yes/no]

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
The [kibit-helper](https://github.com/brunchboy/kibit-helper) package
available from [MELPA](http://melpa.org/) provides several handy
commands. First, make sure you have MELPA available as a source of
packages (which you may well already have done). As described in their
[Getting started](http://melpa.org/#/getting-started) section, put the
following into your `~/.emacs`:

```elisp
(add-to-list 'package-archives
             '("melpa-stable" . "http://stable.melpa.org/packages/") t)
```

(If you want to be more on the cutting edge, you can include unreleased
versions of packages using the non-stable URL, as explained in the
MELPA instructions, but kibit-helper is also available from the less
exciting stable repository.)

This will give you three new commands,

    M-x kibit
    M-x kibit-current-file
    M-x kibit-accept-proposed-change

The first two cause the properly highlighted and hyperlinked kibit output to be
presented in a `*Kibit Suggestions*` buffer. The third lets you automatically
apply most of those suggestions to your source. (Suggestions which cite large
blocks of code including comments cannot be automatically applied, as Kibit
discards comments during processing.)

You will likely want to bind the last function to <kbd>C-x</kbd>
<kbd>C-\`</kbd> so it is easy to alternate with the `next-error`
function (conventionally <kbd>C-x</kbd> <kbd>\`</kbd>) as you walk
through the suggestions made by Kibit:

```elisp
(global-set-key (kbd "C-x C-`") 'kibit-accept-proposed-change)
```

## Usage from inside vim (through Leiningen plugin)

[kibit-vim](https://github.com/fbeline/kibit-vim) allows you to analyze the current opened file by running:

`:Kibit`

## Usage from inside vim (with vim-fireplace)

If you have [vim-fireplace](https://github.com/tpope/vim-fireplace/) installed, you can use [vim-kibit](https://github.com/humorless/vim-kibit) to run Kibit on your current buffer through the running REPL session.

vim-kibit is invoked by running:

`:Kibit`

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
[`control-structures.clj`](https://github.com/jonase/kibit/blob/master/kibit/src/kibit/rules/control_structures.clj)
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
