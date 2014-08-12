package com.moandjiezana.toml.values;

import static com.moandjiezana.toml.values.ValueAnalysis.INVALID;
import static com.moandjiezana.toml.values.ValueParserUtils.isComment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringParser implements ValueParser {
  
  static final StringParser STRING_PARSER = new StringParser();
  private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\u(.*)");

  @Override
  public boolean canParse(String s) {
    return s.startsWith("\"");
  }

  @Override
  public Object parse(String value) {
    int stringTerminator = -1;
    char[] chars = value.toCharArray();

    for (int i = 1; i < chars.length; i++) {
      char ch = chars[i];
      if (ch == '"' && chars[i - 1] != '\\') {
        stringTerminator = i;
        break;
      }
    }

    if (stringTerminator == -1 || !isComment(value.substring(stringTerminator + 1))) {
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
  
  private StringParser() {}
}
