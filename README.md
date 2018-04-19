# toml4j

toml4j is a [TOML 0.4.0](https://github.com/toml-lang/toml/blob/master/versions/en/toml-v0.4.0.md) parser for Java.

Forked from:

https://github.com/mwanji/toml4j

## Installation

Add the following dependency to your POM (or equivalent for other dependency managers):

```xml
<dependency>
  <groupId>com.exonum.toml</groupId>
  <artifactId>toml4j</artifactId>
  <version>${version}</version>
</dependency>
```

Requires Java 1.6 or above.

## Quick start

```java
Toml toml = new Toml().read(getTomlFile());
String someValue = toml.getString("someKey");
Date someDate = toml.getDate("someTable.someDate");
MyClass myClass = toml.to(MyClass.class);
```

## License

Licensed under the [MIT License](LICENSE)
