# toml4j

toml4j is a [TOML](https://github.com/mojombo/toml) parser that uses the [Parboiled](http://www.parboiled.org) PEG parser.

## Installation

toml4j is currently published in snapshot form. Add the following repository to your POM:

````xml
<repositories>
  <repository>
    <id>sonatype-nexus-snapshots</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
````

Add the following dependency:

````xml
<dependency>
  <groupId>com.moandjiezana.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
````

## Usage

1. Create a `com.moandjiezana.toml.Toml` instance
1. Call the `parse` method of your choice
1. Use the getters to retrieve the data

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

### Custom classes

A Toml object can be mapped to a custom class with the `Toml#to(Class<T>)` method.

Any keys not found in both the TOML and the class are ignored.

Key groups can be mapped to other custom classes.

````
name = "Mwanji Ezana"

[address]
  street = "123 A Street"
  city = "AnyVille"
````

````java
  public class Address {
    private String street;
    private String city;
  }

  public class User {
    private String name;
    private Address address;
  }
````

````java
Toml toml = new Toml().parse(tomlFile);
User user = toml.to(User.class);
````

When defaults are present, a shallow merge is performed.

## TODO

* Fail on invalid definitions

## License

toml4j is copyright of Moandji Ezana and is licensed under the [MIT License](http://www.opensource.org/licenses/mit-license.php)

