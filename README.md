# toml4j

toml4j is a [TOML 0.1.0](https://github.com/mojombo/toml) parser for Java that uses the [Parboiled](http://www.parboiled.org) PEG parser.

[![Build Status](https://travis-ci.org/mwanji/toml4j.svg?branch=master)](https://travis-ci.org/mwanji/toml4j)

## Installation

Add the following dependency to your POM:

````xml
<dependency>
  <groupId>com.moandjiezana.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>0.1.0</version>
</dependency>
````

## Usage

### Custom classes

A Toml object can be mapped to a custom class with the `Toml#to(Class<T>)` method.

Any keys not found in both the TOML and the class are ignored.

Key groups can be mapped to other custom classes. Fields may be private.

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
Toml toml = new Toml().parse(tomlFile);
User user = toml.to(User.class);

assert user.name.equals("Mwanji Ezana");
assert user.address.street.equals("123 A Street");
````

When defaults are present, a shallow merge is performed.

### Key names

1. Create a `com.moandjiezana.toml.Toml` instance
2. Call the `parse` method of your choice
3. Use the getters to retrieve the data

````java
Toml toml = new Toml().parse(getTomlFile()); // throws an Exception if the TOML is incorrect

String title = toml.getString("title"); // if a key doesn't exist, returns null
Boolean enabled = toml.getBoolean("database.enabled"); // gets the value of enabled from the database key group
Toml servers = toml.getKeyGroup("servers"); // returns a new Toml instance containing only the key group's values
````

### Defaults

The constructor can be given a set of default values that will be used if necessary.

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

Table arrays are mapped to `List`s with `Toml#getTables(String)`. Custom classes and nested table arrays are supported.

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

toml4j is copyright of Moandji Ezana and is licensed under the [MIT License](http://www.opensource.org/licenses/mit-license.php)

