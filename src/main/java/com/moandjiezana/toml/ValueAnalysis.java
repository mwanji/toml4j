package com.moandjiezana.toml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

class ValueAnalysis {
  static final Object INVALID = new Object();
  private static final List<Object> INVALID_ARRAY = new ArrayList<Object>();

  private static final Pattern BOOLEAN_REGEX = Pattern.compile("(true|false)(.*)");
  private static final Pattern FLOAT_REGEX = Pattern.compile("(-?[0-9\\.]*)(.*)");
  private static final Pattern INTEGER_REGEX = Pattern.compile("(-?[0-9]*)(.*)");
  private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z)(.*)");
  private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\u(.*)");

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
    } else if (isArray(value)) {
      ParboiledParser parser = Parboiled.createParser(ParboiledParser.class);
      ParsingResult<List<Object>> parsingResult = new ReportingParseRunner<List<Object>>(parser.Array()).run(value);
      List<Object> tokens = parsingResult.resultValue;
      List<Object> values = convertList(tokens);

      if (values == INVALID_ARRAY) {
        return INVALID;
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
  }

  private boolean isArray(String s) {
    return s.startsWith("[");
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
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

  private List<Object> convertList(List<Object> tokens) {
    ArrayList<Object> nestedList = new ArrayList<Object>();

    for (Object token : tokens) {
      if (token instanceof String) {
        Object converted = convert(((String) token).trim());
        if (nestedList.isEmpty() || isHomogenousArray(converted, nestedList)) {
          nestedList.add(converted);
        } else {
          return INVALID_ARRAY;
        }
      } else if (token instanceof List) {
        List<Object> convertedList = convertList((List<Object>) token);
        if (convertedList != INVALID_ARRAY) {
          nestedList.add(convertedList);
        } else {
          return INVALID_ARRAY;
        }
      }
    }

    return nestedList;
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
