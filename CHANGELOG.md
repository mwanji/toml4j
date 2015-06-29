# toml4j Changelog

## UNRELEASED

### Changed

* __BREAKING:__ Toml#getList(String), Toml#getTable(String) and Toml#getTables(String) return null when key is not found
* Removed trailing newline from error messages (thanks to __[Zero3](https://github.com/Zero3)__)

### Added

* Support for writing objects to TOML (thanks to __[dilecti](https://github.com/dilecti)__)
* Support for underscores in numbers (the feature branch had accidentally not been merged! :( )
* Set<Map.Entry> Toml#entrySet() cf. Reflection section in README (thanks __[Zero3](https://github.com/Zero3)__ and __[d3xter](https://github.com/d3xter)__)
* Overloaded getters that take a default value (thanks to __[udiabon](https://github.com/udiabon)__)

## 0.4.0 / 2015-02-16

### Changed

* __BREAKING:__ Toml#getList(String) replaced Toml#getList(String, Class) 
* Dropped dependency on Parboiled and its significant transitive dependencies

### Added

* Support for [TOML 0.4.0](https://github.com/toml-lang/toml/blob/master/versions/en/toml-v0.4.0.md)
* Toml#isEmpty()
* More detailed error messages, including line numbers

### Fixed

* Short-form Unicode escape handling
* Exponent handling

## 0.3.1 / 2014-12-16
* Support for [TOML 0.3.1](https://github.com/toml-lang/toml/tree/v0.3.1) spec
* Pass TOML validator (https://github.com/BurntSushi/toml-test), which uncovered many bugs.
* Reduced visibility of internal classes, so that only Toml class is visible to users.
* Refactored parsing into several steps.

## 0.2 / 2014-04-10
* Support for [TOML 0.2](https://github.com/toml-lang/toml/tree/v0.2.0) spec, most notably table arrays.

## 0.1 / 2014-04-06
* Support for [TOML 0.1](https://github.com/toml-lang/toml/tree/v0.1.0) spec.
