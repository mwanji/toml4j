package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.isComment;

import java.util.concurrent.atomic.AtomicInteger;

class MultilineStringConverter implements ValueConverter {

  static final MultilineStringConverter MULTILINE_STRING_PARSER = new MultilineStringConverter();

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("\"\"\"");
  }

  @Override
  public Object convert(String s) {
    AtomicInteger index = new AtomicInteger();
    Object converted = convert(s, index);

    if (converted == INVALID || !isComment(s.substring(index.incrementAndGet()))) {
      return INVALID;
    }

    return converted;
  }

  @Override
  public Object convert(String s, AtomicInteger index) {
    char[] chars = s.toCharArray();
    int startIndex = index.addAndGet(3);
    int endIndex = -1;

    for (int i = startIndex; i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];

      if (c == '"' && chars.length > i + 2 && chars[i + 1] == '"' && chars[i + 2] == '"') {
        endIndex = i;
        index.addAndGet(2);
        break;
      }
    }
    
    if (endIndex == -1) {
      return INVALID;
    }

    s = s.substring(startIndex, endIndex);
    s = s.replaceAll("\\\\\\s+", "");
    s = StringConverter.STRING_PARSER.replaceUnicodeCharacters(s);
    s = StringConverter.STRING_PARSER.replaceSpecialCharacters(s);

    return s;
  }

  private MultilineStringConverter() {
  }

}
