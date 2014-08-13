package com.moandjiezana.toml;

import java.util.List;

import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.BasicParseRunner;

class ValueConverterUtils {
  static final Object INVALID = new Object();
  
  static ValueParser parser() {
    return Parboiled.createParser(ValueParser.class);
  }
  
  static <T> List<T> parse(Rule rule, String s) {
    return new BasicParseRunner<List<T>>(rule).run(s).resultValue;
  }

  static boolean isComment(String line) {
    if (line == null || line.isEmpty()) {
      return true;
    }

    char[] chars = line.toCharArray();

    for (char c : chars) {
      if (Character.isWhitespace(c)) {
        continue;
      }

      return c == '#';
    }

    return false;
  }

  private ValueConverterUtils() {}
}
