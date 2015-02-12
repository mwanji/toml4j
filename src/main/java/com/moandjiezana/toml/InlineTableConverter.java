package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverters.CONVERTERS;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

class InlineTableConverter implements ValueConverter {

  static final InlineTableConverter INLINE_TABLE_PARSER = new InlineTableConverter();
  
  @Override
  public boolean canConvert(String s) {
    return s.startsWith("{");
  }

  @Override
  public Object convert(String s, AtomicInteger sharedIndex, Context context) {
    AtomicInteger line = context.line;
    int startLine = line.get();
    int startIndex = sharedIndex.get();
    boolean inKey = true;
    boolean inValue = false;
    boolean quoted = false;
    boolean terminated = false;
    StringBuilder currentKey = new StringBuilder();
    HashMap<String, Object> results = new HashMap<String, Object>();
    Results.Errors errors = new Results.Errors();
    
    for (int i = sharedIndex.incrementAndGet(); sharedIndex.get() < s.length(); i = sharedIndex.incrementAndGet()) {
      char c = s.charAt(i);
      
      if (c == '"' && inKey) {
        quoted = !quoted;
        currentKey.append(c);
      } else if (quoted) {
        currentKey.append(c);
      } else if (inValue && !Character.isWhitespace(c)) {
        Object converted = CONVERTERS.convert(s, sharedIndex, context.with(Identifier.from(currentKey.toString(), context)));
        
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
          return errors;
        }
        
        results.put(currentKey.toString().trim(), converted);
        currentKey = new StringBuilder();
        inValue = false;
      } else if (c == '{') {
        sharedIndex.incrementAndGet();
        Object converted = convert(s, sharedIndex, context.with(Identifier.from(currentKey.toString(), context)));
        
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
          return errors;
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
      errors.unterminated(context.identifier.getName(), s.substring(startIndex), startLine);
    }
    
    if (errors.hasErrors()) {
      return errors;
    }
    
    return results;
  }

  private InlineTableConverter() {}
}
