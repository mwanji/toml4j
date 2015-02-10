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
    AtomicInteger sharedIndex = new AtomicInteger();
    Object converted = convert(s, sharedIndex);
    
    char[] chars = s.toCharArray();
    
    for (int i = sharedIndex.incrementAndGet(); i < chars.length; i++) {
      char c = chars[i];
      
      if (c == '#') {
        break;
      }
      
      if (!Character.isWhitespace(c)) {
        return INVALID;
      }
    }
    
    return converted;
  }
  
  @Override
  public Object convert(String s, AtomicInteger index) {
    char[] chars = s.toCharArray();
    List<Object> arrayItems = new ArrayList<Object>();
    boolean terminated = false;
    
    for (int i = index.incrementAndGet(); i < chars.length; i = index.incrementAndGet()) {

      char c = chars[i];

      if (Character.isWhitespace(c)) {
        continue;
      }
      if (c == ',') {
        continue;
      }

      if (c == '[') {
        arrayItems.add(convert(s, index));
        continue;
      }

      if (c == ']') {
        terminated = true;
        break;
      }

      arrayItems.add(VALUE_CONVERTERS.convert(s, index));
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    for (Object arrayItem : arrayItems) {
      if (arrayItem == INVALID) {
        return INVALID;
      }
      
      if (!isHomogenousArray(arrayItem, arrayItems)) {
        return INVALID;
      }
    }
    
    return arrayItems;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
  }
  
  private ArrayConverter() {}
}
