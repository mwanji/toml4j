package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import static com.moandjiezana.toml.ValueConverterUtils.parse;
import static com.moandjiezana.toml.ValueConverterUtils.parser;

import java.util.ArrayList;
import java.util.List;

class ArrayConverter implements ValueConverter {

  static final ArrayConverter ARRAY_PARSER = new ArrayConverter();

  private static final List<Object> INVALID_ARRAY = new ArrayList<Object>();
  private static final ValueConverters VALUE_ANALYSIS = new ValueConverters();

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("[");
  }

  @Override
  public Object convert(String s) {
    List<Object> tokens = parse(parser().Array(), s);
    List<Object> values = convertList(tokens);

    if (values == INVALID_ARRAY) {
      return INVALID;
    }

    return values;
  }

  private List<Object> convertList(List<Object> tokens) {
    ArrayList<Object> nestedList = new ArrayList<Object>();

    for (Object token : tokens) {
      if (token instanceof String) {
        Object converted = VALUE_ANALYSIS.convert(((String) token).trim());
        if (converted == INVALID) {
          return INVALID_ARRAY;
        }
        if (isHomogenousArray(converted, nestedList)) {
          nestedList.add(converted);
        } else {
          return INVALID_ARRAY;
        }
      } else if (token instanceof List) {
        @SuppressWarnings("unchecked")
        List<Object> convertedList = convertList((List<Object>) token);
        if (convertedList != INVALID_ARRAY && isHomogenousArray(convertedList, nestedList)) {
          nestedList.add(convertedList);
        } else {
          return INVALID_ARRAY;
        }
      }
    }

    return nestedList;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
  }

  private ArrayConverter() {}
}
