package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

import java.util.HashMap;

class InlineTableConverter implements ValueConverter {

  static final InlineTableConverter INLINE_TABLE_PARSER = new InlineTableConverter();
  private static final ValueConverters CONVERTERS = new ValueConverters();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("{");
  }

  @Override
  public Object convert(String s) {
    char[] chars = s.toCharArray();
    boolean inKey = true;
    boolean pairHasKey = false;
    boolean inValue = false;
    boolean quoted = false;
    boolean inString = false;
    boolean terminated = false;
    StringBuilder currentKey = new StringBuilder();
    StringBuilder current = new StringBuilder();
    HashMap<String, Object> results = new HashMap<String, Object>();
    
    for (int i = 1; i < chars.length; i++) {
      char c = chars[i];
      
      if (terminated) {
        if (Character.isWhitespace(c)) {
          continue;
        }
        if (c == '#') {
          break;
        }
        
        return INVALID;
      }
      
      if (c == '"') {
        quoted = !quoted;
        (inValue ? current : currentKey).append(c);
      } else if (quoted) {
        (inKey ? currentKey : current).append(c);
      } else if (c == ',') {
        Object converted = CONVERTERS.convert(current.toString().trim());
        
        if (converted == INVALID) {
          return INVALID;
        }
        
        results.put(currentKey.toString().trim(), converted);
        inKey = true;
        pairHasKey = false;
        inValue = false;
        currentKey = new StringBuilder();
        current = new StringBuilder();
      } else if (c == '=') {
        inKey = false;
        pairHasKey = true;
        inValue = true;
      } else if (c == '}') {
        terminated = true;
        
        if (current.toString().trim().length() == 0) {
          continue;
        }

        Object converted = CONVERTERS.convert(current.toString().trim());
        
        if (converted == INVALID) {
          return INVALID;
        }
        
        results.put(currentKey.toString().trim(), converted);
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
