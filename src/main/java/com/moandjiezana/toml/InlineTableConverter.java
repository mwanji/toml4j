package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

class InlineTableConverter implements ValueConverter {

  static final InlineTableConverter INLINE_TABLE_PARSER = new InlineTableConverter();
  private static final ValueConverters CONVERTERS = new ValueConverters();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("{");
  }

  @Override
  public Object convert(String s) {
    AtomicInteger sharedIndex = new AtomicInteger(1);
    Object converted = convert(s, sharedIndex);
    char[] chars = s.toCharArray();
    
    for (; sharedIndex.get() < s.length(); sharedIndex.incrementAndGet()) {
      char c = chars[sharedIndex.get()];
      if (Character.isWhitespace(c)) {
        continue;
      }
      if (c == '#') {
        break;
      }
      
      return INVALID;
    }
    
    return converted;
  }

  Object convert(String s, AtomicInteger sharedIndex) {
    char[] chars = s.toCharArray();
    boolean inKey = true;
    boolean inValue = false;
    boolean quoted = false;
    boolean terminated = false;
    StringBuilder currentKey = new StringBuilder();
    StringBuilder current = new StringBuilder();
    HashMap<String, Object> results = new HashMap<String, Object>();
    
    for (; sharedIndex.get() < chars.length; sharedIndex.incrementAndGet()) {
      int i = sharedIndex.get();
      char c = chars[i];
      
      if (c == '"') {
        quoted = !quoted;
        (inValue ? current : currentKey).append(c);
      } else if (quoted) {
        (inKey ? currentKey : current).append(c);
      } else if (c == '[' && inValue) {
        sharedIndex.incrementAndGet();
        Object converted = ArrayConverter.ARRAY_PARSER.convert(s, sharedIndex);
        
        if (converted == INVALID) {
          return INVALID;
        }
        
        results.put(currentKey.toString().trim(), converted);
        i = sharedIndex.get();
        continue;
      } else if (c == '{') {
        sharedIndex.incrementAndGet();
        Object converted = convert(s, sharedIndex);
        
        if (converted == INVALID) {
          return INVALID;
        }
        
        results.put(currentKey.toString().trim(), converted);

        inKey = true;
        inValue = false;
        currentKey = new StringBuilder();
        current = new StringBuilder();
      } else if (c == ',') {
        if (!current.toString().trim().isEmpty()) {
          Object converted = CONVERTERS.convert(current.toString().trim());
          
          if (converted == INVALID) {
            return INVALID;
          }
          
          results.put(currentKey.toString().trim(), converted);
        }

        inKey = true;
        inValue = false;
        currentKey = new StringBuilder();
        current = new StringBuilder();
      } else if (c == '=') {
        inKey = false;
        inValue = true;
      } else if (c == '}') {
        terminated = true;
        
        String trimmed = current.toString().trim();
        if (!trimmed.isEmpty()) {
          Object converted = CONVERTERS.convert(trimmed);
          
          if (converted == INVALID) {
            return INVALID;
          }
          
          results.put(currentKey.toString().trim(), converted);
        }

        sharedIndex.incrementAndGet();
        break;
      } else {
        (inKey ? currentKey : current).append(c);
      }
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    return results;
  }

  private InlineTableConverter() {}
}
