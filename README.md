# toml4j

toml4j is a [TOML 0.3.1](https://github.com/toml-lang/toml/tree/v0.3.1) parser for Java.

[![Build Status](https://travis-ci.org/mwanji/toml4j.svg)](https://travis-ci.org/mwanji/toml4j) [![Coverage Status](https://img.shields.io/coveralls/mwanji/toml4j.svg)](https://coveralls.io/r/mwanji/toml4j)

For the bleeding-edge version integrating the latest specs, see the [work-in-progress branch](https://github.com/mwanji/toml4j/tree/wip).

## Installation

Add the following dependency to your POM (or equivalent for other dependency managers):

````xml
<dependency>
  <groupId>com.moandjiezana.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>0.3.1</version>
</dependency>
````

Requires Java 1.6.

## Quick start

````java
Toml toml = new Toml().parse(getTomlFile());
String someValue = toml.getString("someKey");
Date someDate = toml.getDate("someTable.someDate");
MyClass myClass = toml.to(MyClass.class);
````

## Usage

A `com.moandjiezana.toml.Toml` instance is populated by calling one of `parse(File)`, `parse(InputStream)`, `parse(Reader)` or `parse(String)`.

````java
Toml toml = new Toml().parse("a=1");
````

An exception is thrown if the source is not valid TOML.

The data can then be accessed either by converting the Toml instance to your own class or by accessing tables and keys by name.

### Custom classes

`Toml#to(Class<T>)` maps a Toml instance to the given class.

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

Any keys not found in both the TOML and the class are ignored. Fields may be private.

All TOML primitives can be mapped, as well as a number of Java-specific types:

* A TOML Number can be converted to any primitive type (or the wrapper equivalent), `BigInteger` or `BigDecimal`
* A TOML string can be converted to a `String`, enum, `java.net.URI` or `java.net.URL`
* A single-letter TOML string can be converted to a `char` or `Character`
* Multiline and literal TOML strings can be converted to `String`
* A TOML array can be converted to a `List`, `Set` or array. The generic type can be anything that can be converted.
* A TOML table can be converted to a custom class or to a `Map<String, Object>`. The generic type of the value can be anything that can be converted.

Custom classes, Maps and collections thereof can be nested to any level. See [TomlToClassTest#should_convert_fruit_table_array()](src/test/java/com/moandjiezana/toml/TomlToClassTest.java) for an example.

### Key names

Use the getters to retrieve the data:

* `getString(String)`
* `getDate(String)`
* `getBoolean(String)`
* `getLong(String)`
* `getDouble(String)`
* `getList(String, Class<T>)`
* `getTable(String)` returns a new Toml instance containing only the keys in that table.
* `getTables(String)`, for table arrays, returns `List<Toml>`. 

You can also navigate values within a table with a compound key of the form `table.key`. Use a zero-based index such as `tableArray[0].key` to navigate table arrays.

Non-existent keys return null.

````
title = "TOML Example"

[database]
  ports = [ 8001, 8001, 8002 ]
  enabled = true
  [database.credentials]
    password = "password"
    
[servers]
  cluster = "hyades"
  [servers.alpha]
  ip = "10.0.0.1"
  
[[networks]]
  name = "Level 1"
  [networks.status]
    bandwidth = 10

[[networks]]
  name = "Level 2"

[[networks]]
  name = "Level 3"
  [[networks.operators]]
    location = "Geneva"
  [[networks.operators]]
    location = "Paris"
````

````java
Toml toml = new Toml().parse(getTomlFile());

String title = toml.getString("title");
Boolean enabled = toml.getBoolean("database.enabled");
List<Long> ports = toml.getList("database.ports", Long.class);
String password = toml.getString("database.credentials.password");

Toml servers = toml.getTable("servers");
String cluster = servers.getString("cluster"); // navigation is relative to current Toml instance
String ip = servers.getString("alpha.ip");

Toml network1 = toml.getTable("networks[0]");
String network2Name = toml.getString("networks[1].name"); // "Level 2"
List<Toml> network3Operators = toml.getTables("networks[2].operators");
String network3Operator2Location = toml.getString("networks[2].operators[1].location"); // "Paris"
````

### Defaults

The constructor can be given a set of default values that will be used as fallbacks. For tables and table arrays, a shallow merge is performed.

````
# defaults
a = 2
b = 3

[table]
  c = 4
  d = 5
````

````
a = 1

[table]
  c = 2
  
[[array]]
  d = 3
````

````java
Toml defaults = new Toml().parse(getDefaultsFile());
Toml toml = new Toml(defaults).parse(getTomlFile());

Long a = toml.getLong("a"); // returns 1, not 2
Long b = toml.getLong("b"); // returns 3
Long c = toml.getLong("c"); // returns null
Long tableC = toml.getLong("table.c"); // returns 2, not 4
Long tableD = toml.getLong("table.d"); // returns null, not 5
Long arrayD = toml.getLong("array[0].d"); // returns 3
````

### Limitations

Date precision is limited to milliseconds.

## License

toml4j is copyright of Moandji Ezana and is licensed under the [MIT License](LICENSE)
