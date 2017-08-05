# toml4j Changelog

## 0.7.2 / 2017-08-05

## Updated

* [Update Gson to 2.8.1 and various Maven plugins to latest versions](https://github.com/mwanji/toml4j/pull/45) (thanks to __[DanilaFe](https://github.com/DanilaFe)__)

## Fixed

* [tomlWriter.write NullPointerException in JDK9](https://github.com/mwanji/toml4j/issues/46) (thanks to __[iwangxiaodong](https://github.com/iwangxiaodong)__)
* Change build to be able to release a new version entirely from the command line

## 0.7.1 / 2016-07-27

* [Support literal strings in table names](https://github.com/mwanji/toml4j/issues/36) (thanks to __[bruno-medeiros](https://github.com/bruno-medeiros)__)

## 0.7.0 / 2016-07-12

## Added

* Toml#read(Toml) merges two Toml instances (thanks to __[gustavkarlsson](https://github.com/gustavkarlsson)__)

## 0.6.0 / 2016-06-14

## Added

* Toml#toMap() convenience method (thanks to __[andytill](https://github.com/andytill)__ and __[Gyscos](https://github.com/Gyscos)) 

## Fixed

* Transient fields are not written to TOML files (thanks to __[lare96](https://github.com/lare96)__)
* Support positive timezone offset in datetime (thanks to __[aloyse](https://github.com/aloyse)__)

## 0.5.1 / 2016-01-24

### Fixed

* [Handling of tables with same name in different table array items](https://github.com/mwanji/toml4j/issues/26) (thanks to __[stanofujdiar](https://github.com/stanofujdiar)__)

## 0.5.0 / 2015-12-10

### Changed

* __BREAKING:__ Toml#parse methods renamed to read
* __BREAKING:__ Toml#getList(String), Toml#getTable(String) and Toml#getTables(String) return null when key is not found
* Removed trailing newline from error messages (thanks to __[Zero3](https://github.com/Zero3)__)
* Toml#read(File) forces encoding to UTF-8 (thanks to __[Bruno Medeiros](https://github.com/bruno-medeiros)__)

### Added

* Support for writing objects to TOML with TomlWriter (thanks to __[dilecti](https://github.com/dilecti)__)
* Support for underscores in numbers (the feature branch had accidentally not been merged into 0.4.0! :( )
* Set<Map.Entry> Toml#entrySet() cf. Reflection section in README (thanks __[Zero3](https://github.com/Zero3)__ and __[d3xter](https://github.com/d3xter)__)
* Overloaded getters that take a default value (thanks to __[udiabon](https://github.com/udiabon)__)
* Toml#contains(String) and Toml#containsXXX(String) methods to check for existence of keys

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
