package com.moandjiezana.toml;

interface ValueParser {

  boolean canParse(String s);
  Object parse(String s);
}
