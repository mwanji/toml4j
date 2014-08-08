package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ValueAnalysis {
  static final Object INVALID = new Object();

  private static final Pattern STRING_REGEX = Pattern.compile("\"(.*)\"(.*)");
  private static final Pattern BOOLEAN_REGEX = Pattern.compile("(true|false)(.*)");
  private static final Pattern FLOAT_REGEX = Pattern.compile("(-?[0-9\\.]*)(.*)");
  private static final Pattern INTEGER_REGEX = Pattern.compile("(-?[0-9]*)(.*)");
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)(.*)");
  private static final Pattern LIST_REGEX = Pattern.compile("(\\[(.*)\\])(.*)");
  private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\u(.*)");
  private static final Pattern RESERVED_CHARACTER_REGEX = Pattern.compile("\\\\[^bfntr\"/\\\\]");

  private final String rawValue;
  private Matcher chosenMatcher;

  public ValueAnalysis(String value) {
    this.rawValue = value.trim();
  }

  public Object getValue() {
    return convert(rawValue);
  }

  private Object convert(String value) {
    if (isString(value)) {
      return convertString(value);
    } else if (isInteger(value)) {
      return Long.valueOf(chosenMatcher.group(1));
    } else if (isFloat(value)) {
      return Double.valueOf(chosenMatcher.group(1));
    } else if (isBoolean(value)) {
      return Boolean.valueOf(chosenMatcher.group(1));
    } else if (isList(value)) {
      ArrayList<Object> values = new ArrayList<Object>();
      value = chosenMatcher.group(1);
      String[] split = value.substring(1,  value.length() - 1).split(",");
      for (String s : split) {
        Object converted = convert(s.trim());
        if (values.isEmpty() || values.get(0).getClass().isAssignableFrom(converted.getClass()) || converted.getClass().isAssignableFrom(values.get(0).getClass())) {
          values.add(converted);
        } else {
          return INVALID;
        }
      }

      return values;
    } else if (isDate(value)) {
      String s = chosenMatcher.group(1).replace("Z", "+00:00");
      try {
        s = s.substring(0, 22) + s.substring(23);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setLenient(false);
        return dateFormat.parse(s);
      } catch (Exception e) {
        return INVALID;
      }
    } else {
      return INVALID;
    }
  }

  private boolean isString(String value) {
    return value.startsWith("\"");
  }

  private boolean isFloat(String value) {
    Matcher matcher = FLOAT_REGEX.matcher(value);
    if (matcher.matches() && isComment(matcher.group(2))) {
      chosenMatcher = matcher;

      return true;
    }

    return false;
//    char[] chars = value.toCharArray();
//
//    for (int i = 0; i < chars.length; i++) {
//      char ch = chars[i];
//      if (Character.isDigit(ch) || ch == '.' || (i == 0 && ch == '-')) {
//        continue;
//      }
//
//      return false;
//    }
//
//    return true;
  }

  private boolean isDate(String value) {
    Matcher matcher = DATE_REGEX.matcher(value);
    if (matcher.matches()) {
      chosenMatcher = matcher;
      return true;
    }

    return false;
  }

  private boolean isInteger(String s) {
    Matcher matcher = INTEGER_REGEX.matcher(s);

    if (matcher.matches() && isComment(matcher.group(2))) {
      chosenMatcher = matcher;

      return true;
    }

    return false;
//    char[] chars = s.toCharArray();
//
//    for (int i = 0; i < chars.length; i++) {
//      if (Character.isDigit(chars[i]) || (i == 0 && chars[i] == '-')) {
//        continue;
//      }
//
//      return false;
//    }
//
//    return true;
  }

  private boolean isList(String s) {
    Matcher matcher = LIST_REGEX.matcher(s);

    if (matcher.matches()) {
      chosenMatcher = matcher;

      return true;
    }

    return false;
  }

  private List<String> tokenizeList(String list) {
    ArrayList<String> strings = new ArrayList<String>();
    char[] chars = list.toCharArray();
    int openIndex = -1;

    for (int i = 0; i < chars.length && openIndex < 0; i++) {

    }

    StringBuilder token = new StringBuilder();
    boolean ignore = false;
    for (int i = 0; i < chars.length; i++) {
      if (ignore) {
        continue;
      }
      if (chars[i] == '[')
      if (chars[i] != ',') {
        token.append(chars[i]);
      } else {
        strings.add(token.toString().trim());
        token = new StringBuilder();
      }
    }

    return strings;
  }

  private boolean isBoolean(String s) {
    Matcher matcher = BOOLEAN_REGEX.matcher(s);

    if (matcher.matches()) {
      chosenMatcher = matcher;
      return true;
    }

    return false;
  }

  private boolean isComment(String line) {
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

  private Object convertString(String value) {
    int stringTerminator = -1;
    int startOfComment = -1;
    char[] chars = value.toCharArray();

    for (int i = 1; i < chars.length; i++) {
      char ch = chars[i];
      if (ch == '"' && chars[i - 1] != '\\') {
        stringTerminator = i;
        break;
      }
    }

    if (stringTerminator == -1) {
      return INVALID;
    }

    value = value.substring(1, stringTerminator);
    value = replaceUnicodeCharacters(value);

    chars = value.toCharArray();
    for (int i = 0; i < chars.length - 1; i++) {
      char ch = chars[i];
      char next = chars[i + 1];

      if (ch == '\\' && next == '\\') {
        i++;
      } else if (ch == '\\' && !(next == 'b' || next == 'f' || next == 'n' || next == 't' || next == 'r' || next == '"' || next == '/' || next == '\\')) {
        return INVALID;
      }
    }

    value = replaceSpecialCharacters(value);

    return value;
  }

  private String replaceUnicodeCharacters(String value) {
    Matcher unicodeMatcher = UNICODE_REGEX.matcher(value);

    while (unicodeMatcher.find()) {
      value = value.replace(unicodeMatcher.group(), new String(Character.toChars(Integer.parseInt(unicodeMatcher.group(1), 16))));
    }
    return value;
  }

  private String replaceSpecialCharacters(String value) {
    return value.replace("\\n", "\n")
      .replace("\\\"", "\"")
      .replace("\\t", "\t")
      .replace("\\r", "\r")
      .replace("\\\\", "\\")
      .replace("\\/", "/")
      .replace("\\b", "\b")
      .replace("\\f", "\f");
  }
}
