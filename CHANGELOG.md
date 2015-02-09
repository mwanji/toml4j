# toml4j Changelog

## Unreleased

### Changed

* Toml#getList(String) replaced Toml#getList(String, Class) 
* Dropped dependency on Parboiled and its significant transitive dependencies
* Updated Gson to 2.3.1

### Added

* Line numbers included in error messages

### Fixed

* Fixed short-form Unicode escapes
* Fixed exponent handling

## 0.3.1 / 2014-12-16
* Support for [TOML 0.3.1](https://github.com/toml-lang/toml/tree/v0.3.1) spec
* Pass TOML validator (https://github.com/BurntSushi/toml-test), which uncovered many bugs.
* Reduced visibility of internal classes, so that only Toml class is visible to users.
* Refactored parsing into several steps.

## 0.2 / 2014-04-10
* Support for [TOML 0.2](https://github.com/toml-lang/toml/tree/v0.2.0) spec, most notably table arrays.

## 0.1 / 2014-04-06
* Support for [TOML 0.1](https://github.com/toml-lang/toml/tree/v0.1.0) spec.
