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
    AtomicInteger index = new AtomicInteger();
    Object converted = convert(s, index);
    
    String substring = s.substring(index.incrementAndGet());
    if (converted == INVALID || !ValueConverterUtils.isComment(substring)) {
      return INVALID;
    }
    
    return converted;
  }

  @Override
  public Object convert(String s, AtomicInteger sharedIndex) {
    char[] chars = s.toCharArray();
    boolean inKey = true;
    boolean inValue = false;
    boolean quoted = false;
    boolean terminated = false;
    StringBuilder currentKey = new StringBuilder();
    HashMap<String, Object> results = new HashMap<String, Object>();
    
    for (int i = sharedIndex.incrementAndGet(); sharedIndex.get() < chars.length; i = sharedIndex.incrementAndGet()) {
      char c = chars[i];
      
      if (c == '"' && inKey) {
        quoted = !quoted;
        currentKey.append(c);
      } else if (quoted) {
        currentKey.append(c);
      } else if (inValue && !Character.isWhitespace(c)) {
        Object converted = CONVERTERS.convert(s, sharedIndex);
        
        if (converted == INVALID) {
          return INVALID;
        }
        
        results.put(currentKey.toString().trim(), converted);
        currentKey = new StringBuilder();
        inValue = false;
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
      } else if (c == ',') {
        inKey = true;
        inValue = false;
        currentKey = new StringBuilder();
      } else if (c == '=') {
        inKey = false;
        inValue = true;
      } else if (c == '}') {
        terminated = true;
        break;
      } else if (inKey) {
        currentKey.append(c);
      }
    }
    
    if (!terminated) {
      return INVALID;
    }
    
    return results;
  }

  private InlineTableConverter() {}
}
