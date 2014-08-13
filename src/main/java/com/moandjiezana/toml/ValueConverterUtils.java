package com.moandjiezana.toml;

class ValueConverterUtils {
  static final Object INVALID = new Object();

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
