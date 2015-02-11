package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringConverter implements ValueConverter {
  
  static final StringConverter STRING_PARSER = new StringConverter();
  private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\[uU](.{4})");

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("\"");
  }

  @Override
  public Object convert(String value, AtomicInteger sharedIndex, Context context) {
    int startIndex = sharedIndex.incrementAndGet();
    int endIndex = -1;
    char[] chars = value.toCharArray();

    for (int i = sharedIndex.get(); i < chars.length; i = sharedIndex.incrementAndGet()) {
      char ch = chars[i];
      if (ch == '"' && chars[i - 1] != '\\') {
        endIndex = i;
        break;
      }
    }

    if (endIndex == -1) {
      Results.Errors errors = new Results.Errors();
      errors.unterminated(context.identifier.getName(), value.substring(startIndex - 1), context.line.get());
      return errors;
    }
    
    String raw = value.substring(startIndex, endIndex);
    value = replaceUnicodeCharacters(raw);
    value = replaceSpecialCharacters(value);
    
    if (value == null) {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), raw, context.line.get());
      return errors;
    }

    return value;
  }

  String replaceUnicodeCharacters(String value) {
    Matcher unicodeMatcher = UNICODE_REGEX.matcher(value);

    while (unicodeMatcher.find()) {
      value = value.replace(unicodeMatcher.group(), new String(Character.toChars(Integer.parseInt(unicodeMatcher.group(1), 16))));
    }
    return value;
  }

  String replaceSpecialCharacters(String value) {
    char[] chars = value.toCharArray();
    for (int i = 0; i < chars.length - 1; i++) {
      char ch = chars[i];
      char next = chars[i + 1];

      if (ch == '\\' && next == '\\') {
        i++;
      } else if (ch == '\\' && !(next == 'b' || next == 'f' || next == 'n' || next == 't' || next == 'r' || next == '"' || next == '\\')) {
        return null;
      }
    }

    return value.replace("\\n", "\n")
      .replace("\\\"", "\"")
      .replace("\\t", "\t")
      .replace("\\r", "\r")
      .replace("\\\\", "\\")
      .replace("\\/", "/")
      .replace("\\b", "\b")
      .replace("\\f", "\f");
  }
  
  private StringConverter() {}
}
