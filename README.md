# toml4j

toml4j is a [TOML 0.4.0](https://github.com/toml-lang/toml/blob/master/versions/en/toml-v0.4.0.md) parser for Java.

[![Maven Central](https://img.shields.io/maven-central/v/com.moandjiezana.toml/toml4j.svg)](https://search.maven.org/#search|gav|1|g%3A%22com.moandjiezana.toml%22%20AND%20a%3A%22toml4j%22) [![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE) [![Build Status](https://img.shields.io/travis/mwanji/toml4j)](https://travis-ci.org/mwanji/toml4j) [![Coverage Status](https://img.shields.io/coveralls/mwanji/toml4j.svg)](https://coveralls.io/r/mwanji/toml4j) [![Dependency Status](https://www.versioneye.com/user/projects/54e6364ad1ec573c99000713/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54e6364ad1ec573c99000713)

For the bleeding-edge version integrating the latest specs, see the [work-in-progress branch](https://github.com/mwanji/toml4j/tree/wip).

## Installation

Add the following dependency to your POM (or equivalent for other dependency managers):

```xml
<dependency>
  <groupId>com.moandjiezana.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>0.4.0</version>
</dependency>
```

Requires Java 1.6.

## Quick start

```java
Toml toml = new Toml().parse(getTomlFile());
String someValue = toml.getString("someKey");
Date someDate = toml.getDate("someTable.someDate");
MyClass myClass = toml.to(MyClass.class);
```

## Usage

A `com.moandjiezana.toml.Toml` instance is populated by calling one of `parse(File)`, `parse(InputStream)`, `parse(Reader)` or `parse(String)`.

```java
Toml toml = new Toml().parse("a=1");
```

An exception is thrown if the source is not valid TOML.

The data can then be accessed either by converting the Toml instance to your own class or by accessing tables and keys by name.

### Custom classes

`Toml#to(Class<T>)` maps a Toml instance to the given class.

```toml
name = "Mwanji Ezana"

[address]
  street = "123 A Street"
  city = "AnyVille"
  
[contacts]
  "email address" = "me@example.com"
```

```java
class Address {
  String street;
  String city;
}

class User {
  String name;
  Address address;
  Map<String, Object> contacts;
}
```

```java
User user = new Toml().parse(tomlFile).to(User.class);

assert user.name.equals("Mwanji Ezana");
assert user.address.street.equals("123 A Street");
assert user.contacts.get("\"email address\"").equals("me@example.com");
```

Any keys not found in both the TOML and the class are ignored. Fields may be private.

Quoted keys cannot be mapped directly to a Java object, but they can be used as keys within a `Map`.

TOML primitives can be mapped to a number of Java types:

TOML | Java
---- | ----
Integer | `int`, `long` (or wrapper), `java.math.BigInteger`
Float | `float`, `double` (or wrapper), `java.math.BigDecimal`
String | `String`, enum, `java.net.URI`, `java.net.URL`
One-letter String | `char`, `Character`
Multiline and Literal Strings | `String`
Array | `List`, `Set`, array. The generic type can be anything that can be converted.
Table | Custom class, `Map<String, Object>`

Custom classes, Maps and collections thereof can be nested to any level. See [TomlToClassTest#should_convert_fruit_table_array()](src/test/java/com/moandjiezana/toml/TomlToClassTest.java) for an example.

### Key names

Use the getters to retrieve the data:

* `getString(String)`
* `getDate(String)`
* `getBoolean(String)`
* `getLong(String)`
* `getDouble(String)`
* `getList(String)`
* `getTable(String)` returns a new Toml instance containing only the keys in that table.
* `getTables(String)`, for table arrays, returns `List<Toml>`. 

You can also navigate values within a table with a compound key of the form `table.key`. Use a zero-based index such as `tableArray[0].key` to navigate table arrays.

Non-existent keys return null.

When retrieving quoted keys, the quotes must be used and the key must be spelled exactly the same way, including quotes and whitespace. The only exceptions are Unicode escapes: `"\u00B1" = "value"` would be retrieved with `toml.getString("\"±\"")`.

```toml
title = "TOML Example"
"sub title" = "Now with quoted keys"

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
```

```java
Toml toml = new Toml().parse(getTomlFile());

String title = toml.getString("title");
String subTitle = toml.getString("\"sub title\"");
Boolean enabled = toml.getBoolean("database.enabled");
List<Long> ports = toml.getList("database.ports");
String password = toml.getString("database.credentials.password");

Toml servers = toml.getTable("servers");
String cluster = servers.getString("cluster"); // navigation is relative to current Toml instance
String ip = servers.getString("alpha.ip");

Toml network1 = toml.getTable("networks[0]");
String network2Name = toml.getString("networks[1].name"); // "Level 2"
List<Toml> network3Operators = toml.getTables("networks[2].operators");
String network3Operator2Location = toml.getString("networks[2].operators[1].location"); // "Paris"
```

### Defaults

The constructor can be given a set of default values that will be used as fallbacks. For tables and table arrays, a shallow merge is performed.

You can also call an overloaded version of the getters that take a default value. Note that the default value provided in the constructor take precedence over the one provided by the getter.

```toml
# defaults
a = 2
b = 3

[table]
  c = 4
  d = 5
```

```toml
a = 1

[table]
  c = 2
  
[[array]]
  d = 3
```

```java
Toml defaults = new Toml().parse(getDefaultsFile());
Toml toml = new Toml(defaults).parse(getTomlFile());

Long a = toml.getLong("a"); // returns 1, not 2
Long b = toml.getLong("b"); // returns 3, taken from defaults provided to constructor
Long bPrefersConstructor = toml.getLong("b", 5); // returns 3, not 5
Long c = toml.getLong("c"); // returns null
Long cWithDefault = toml.getLong("c", 5); // returns 5
Long tableC = toml.getLong("table.c"); // returns 2, not 4
Long tableD = toml.getLong("table.d"); // returns null, not 5, because of shallow merge
Long arrayD = toml.getLong("array[0].d"); // returns 3
```

### Serialization

You can serialize a `Toml` or any arbitrary object to a TOML string.

Once you have populated a `Toml` via `Toml.parse()`, you can serialize it back to TOML.

```java
Toml toml = new Toml().parse("a=1");
String tomlString = toml.serialize();
```

Or you can serialize any object.

```java
class AClass {
  int anInt = 1;
  int[] anArray = { 2, 3 };
}

String tomlString = Toml.serializeFrom(new AClass());

/*
yields:
anInt = 1
anArray = [ 2, 3 ]
*/
```

### Limitations

Date precision is limited to milliseconds.

## Contributing

Please see CONTRIBUTING.md.

## License

toml4j is copyright (c) 2013-2015 Moandji Ezana and is licensed under the [MIT License](LICENSE)
