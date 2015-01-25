package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ArrayConverter implements ValueConverter {

  static final ArrayConverter ARRAY_PARSER = new ArrayConverter();

  private static final ValueConverters VALUE_CONVERTERS = new ValueConverters();

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("[");
  }

  @Override
  public Object convert(String s) {
    return convert(s, new AtomicInteger(1), true);
  }
  
  public Object convert(String s, AtomicInteger sharedIndex, boolean topLevel) {
    char[] chars = s.toCharArray();
    List<Object> arrayItems = new ArrayList<Object>();
    boolean terminated = false;
    StringType stringType = StringType.NONE;
    StringBuilder current = new StringBuilder();
    
    for (int i = 1; i < chars.length; i++, sharedIndex.incrementAndGet()) {
      char c = chars[i];

      if (terminated && !topLevel) {
        break;
      }

      if (terminated) {
        if (c == '#') {
          break;
        }
        if (!Character.isWhitespace(c)) {
          return INVALID;
        }
        continue;
      }

      if (stringType == StringType.NONE) {
        if (c == ',') {
          if (current.toString().trim().length() > 0) {
            arrayItems.add(current.toString());
          }
          current = new StringBuilder();
          continue;
        }

        if (c == '[') {
          arrayItems.add(convert(s.substring(i), sharedIndex, false));
          i = sharedIndex.get();
          continue;
        }

        if (c == ']') {
          terminated = true;
          if (current.toString().trim().length() > 0) {
            arrayItems.add(current.toString());
          }
          current = new StringBuilder();
          continue;
        }
      }

      if (c == '"' && chars[i - 1] != '\\' && !stringType.accepts(c)) {
        if (chars.length > i + 2 && chars[i + 1] == c && chars[i + 2] == c) {
          stringType = stringType.flip(StringType.MULTILINE);
        } else {
          stringType = stringType.flip(StringType.BASIC);
        }
      }
      
      if (c == '\'' && !stringType.accepts(c)) {
        if (chars.length > i + 2 && chars[i + 1] == c && chars[i + 2] == c) {
          stringType = stringType.flip(StringType.MULTILINE_LITERAL);
        } else {
          stringType = stringType.flip(StringType.LITERAL);
        }
      }

      current.append(c);
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    return convertList(arrayItems);
  }

  private Object convertList(List<Object> tokens) {
    ArrayList<Object> nestedList = new ArrayList<Object>();

    for (Object token : tokens) {
      if (token instanceof String) {
        Object converted = VALUE_CONVERTERS.convert(((String) token).trim());
        if (converted == INVALID) {
          return INVALID;
        }
        if (isHomogenousArray(converted, nestedList)) {
          nestedList.add(converted);
        } else {
          return INVALID;
        }
      } else if (token instanceof List) {
        @SuppressWarnings("unchecked")
        List<Object> convertedList = (List<Object>) token;
        if (isHomogenousArray(convertedList, nestedList)) {
          nestedList.add(convertedList);
        } else {
          return INVALID;
        }
      }
    }

    return nestedList;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
  }
  
  private static enum StringType {
    NONE, BASIC, LITERAL, MULTILINE, MULTILINE_LITERAL;
    
    StringType flip(StringType to) {
      return this == NONE ? to : NONE;
    }
    
    boolean accepts(char c) {
      if (this == BASIC || this == MULTILINE) {
        return c != '"';
      }
      
      if (this == LITERAL || this == MULTILINE_LITERAL) {
        return c != '\'';
      }
      
      return false;
    }
  }

  private ArrayConverter() {}
}
