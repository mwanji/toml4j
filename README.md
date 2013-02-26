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

````java
Toml toml = new Toml().parse(getTomlFile()); // throws an Exception if the TOML is incorrect

String title = toml.getString("title"); // if a key doesn't exist, returns null
Boolean enabled = toml.getBoolean("database.enabled"); // gets the key enabled from the key group database
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

## License

toml4j is copyright of Moandji Ezana
toml4j is licensed under the [MIT License](http://www.opensource.org/licenses/mit-license.php)

