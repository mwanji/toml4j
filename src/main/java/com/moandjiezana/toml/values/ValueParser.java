package com.moandjiezana.toml.values;

public interface ValueParser {

  boolean canParse(String s);
  Object parse(String s);
}
