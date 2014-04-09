# toml4j

toml4j is a [TOML 0.1.0](https://github.com/mojombo/toml/tree/v0.1.0) parser for Java that uses the [Parboiled](http://www.parboiled.org) PEG parser.

[TOML 0.2.0](https://github.com/mojombo/toml/tree/v0.2.0) support is under development on the master branch.

[![Build Status](https://travis-ci.org/mwanji/toml4j.svg?branch=master)](https://travis-ci.org/mwanji/toml4j)

## Installation

Add the following dependency to your POM (or equivalent for other dependency managers):

````xml
<dependency>
  <groupId>com.moandjiezana.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>0.1.0</version>
</dependency>
````

## Quick start

````java
Toml toml = new Toml().parse(getTomlFile());
String someValue = toml.getString("someKey");
Date someDate = toml.getDate("someKeyGroup.someDate");
MyClass myClass = toml.to(MyClass.class);
````

## Usage

A `com.moandjiezana.toml.Toml` instance is populated by calling one of `parse(File)`, `parse(InputStream)`, `parse(Reader)` or `parse(String)`.

````java
Toml toml = new Toml().parse("a=1");
````

An exception is thrown if the file is not valid TOML.

The data can then be accessed either by converting the Toml instance to your own class or by accessing tables and keys by name.

### Custom classes

`Toml#to(Class<T>)` maps a Toml instance to the given class.

Any keys not found in both the TOML and the class are ignored.

Key groups can be mapped to other custom classes. Fields may be private.

All TOML primitives can be mapped, as well as a number of Java-specific types:

* A TOML Number can be converted to any primitive type (or the wrapper equivalent), `BigInteger` or `BigDecimal`
* A single-letter TOML string can be converted to a `Character`
* A TOML string can be converted to an enum or a `java.net.URL`
* A TOML array can be converted to a `Set`
* A TOML table can be converted to a `Map<String, Object>`

````
name = "Mwanji Ezana"

[address]
  street = "123 A Street"
  city = "AnyVille"
````

````java
class Address {
  String street;
  String city;
}

class User {
  String name;
  Address address;
}
````

````java
User user = new Toml().parse(tomlFile).to(User.class);

assert user.name.equals("Mwanji Ezana");
assert user.address.street.equals("123 A Street");
````

When defaults are present, a shallow merge is performed.

### Key names

Use the getters to retrieve the data:

* `getString(String)`
* `getDate(String)`
* `getBoolean(String)`
* `getLong(String)`
* `getDouble(String)`
* `getList(String, Class<T>)`

Key groups can be accessed with `getKeyGroup(String)`, which returns a new Toml instance containing only the keys in that key group.

You can directly access values within a key group with a compound key:

````java
String s = toml.getString("keygroup1.keygroup2.key");
````

Non-existant keys return null.

````
title = "TOML Example"

[database]
  ports = [ 8001, 8001, 8002 ]
  enabled = true

[servers]
  cluster = "hyades"
  [servers.alpha]
  ip = "10.0.0.1"
````

````java
Toml toml = new Toml().parse(getTomlFile());

String title = toml.getString("title");
Boolean enabled = toml.getBoolean("database.enabled");
List<Long> ports = toml.getList("database.ports", Long.class);
Toml servers = toml.getKeyGroup("servers");
String cluster = servers.getString("cluster");
String ip = servers.getString("alpha.ip");

````

### Defaults

The constructor can be given a set of default values that will be used as fallbacks.

````java
Toml defaults = new Toml().parse("a = 2\nb = 3");
Toml toml = new Toml(defaults).parse("a = 1");

Long a = toml.getLong("a"); // returns 1, not 2
Long b = toml.getLong("b"); // returns 3
Long c = toml.getLong("c"); // returns null
````

## TODO

* Fail on invalid definitions

## Coming in 0.2.0

### Table arrays

Table arrays are mapped to `List<Toml>`s with `Toml#getTables(String)`. Custom classes and nested table arrays are supported.

````
[[products]]
  name = "Hammer"
  sku = 738594937

[[products]]
  name = "Nail"
  sku = 284758393
  color = "gray"
````

````java
Toml toml = new Toml().parse(getTomlFile());

List<Toml> tables = toml.getTables("products");
tables.get(1).getLong("sku"); // returns 284758393
````

## License

toml4j is copyright of Moandji Ezana and is licensed under the [MIT License](LICENSE)

