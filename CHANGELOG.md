# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


## [Unreleased]
### Added
- _CHANGELOG.md_ created.
### Changed
- Something has been changed.
### Fixed
- Something has been fixed.
### Removed
- Something has been removed.

## [0.3.0] - 2019-04-18
### Added
* Added a file path into an error message while reading a file fixes [#205](https://github.com/jonase/kibit/issues/205). [#212](https://github.com/jonase/kibit/pull/212)
* Use :repositories from original project in synthetic project.clj. [#222](https://github.com/jonase/kibit/pull/222)

## [0.1.6] - 2018-11-08
* A long awaited feature/fix - Kibit now supports reading namespaced keywords correctly. A very special thanks to Alex Redington who took this tricky task on. [#198](https://github.com/jonase/kibit/pull/198).
* Make Kibit work with local-repos. [#195](https://github.com/jonase/kibit/pull/195)
* Fixup the monkeypatching. [#192](https://github.com/jonase/kibit/pull/192)
* Add alias support to the reader
* Improve source path handling to prevent checking duplicates

## [0.1.5] - 2017-05-02

* 0.1.4, but released properly.

## [0.1.4] - 2017-05-05

### Additions

* Automatic replacement of suggestions (`--replace` and `--interactive` cli arguments)
* Rules for using `run!` instead of `(dorun (map f coll))`

## [0.1.3] - 2016-11-21
### Additions

* Enabled Emacs' next error function to go to next Kibit suggestion. See the updated code in the README for the change.
* #172 Kibit can now handle sets without crashing!
* #152 Send exceptions to STDERR instead of STDOUT
* New rules (#154, #165, )
* #168 Bumped to new versions of clojure and tools.cli dependencies
* #171 Update core.logic to avoid exception from spec

## [0.1.2] - 2015-04-21
### Additions
* Clojurescript/Cljx support (cljc support coming soon). This just works™, kibit will pick up your source paths from your `project.clj`'s `:source-paths`, `[:cljsbuild :builds]`, and `[:cljx :builds]`.
* Non-zero exit codes. Kibit now exits non-zero when one or more suggestions are made. This is particularly useful for those running checks in a CI environment.
* You can now run kibit on any Clojure project without a project.clj file. Just call `lein kibit` with any number of files and folders and it will inspect the Clojure files contained within.

[Unreleased]: https://github.com/gorillalabs/kibit/compare/v0.3.0...HEAD
