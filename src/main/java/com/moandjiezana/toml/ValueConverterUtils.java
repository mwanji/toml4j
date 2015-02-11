package com.moandjiezana.toml;


class ValueConverterUtils {
  static final Object INVALID = new Object();
  
  static Unterminated unterminated(String payload) {
    return new Unterminated(payload);
  }
  
  static class Unterminated {
    final String payload;

    private Unterminated(String payload) {
      this.payload = payload;
    }
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
