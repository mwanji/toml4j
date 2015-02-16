package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverters.CONVERTERS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ArrayConverter implements ValueConverter {

  static final ArrayConverter ARRAY_PARSER = new ArrayConverter();

  @Override
  public boolean canConvert(String s) {
    return s.startsWith("[");
  }

  @Override
  public Object convert(String s, AtomicInteger index, Context context) {
    AtomicInteger line = context.line;
    int startLine = line.get();
    int startIndex = index.get();
    List<Object> arrayItems = new ArrayList<Object>();
    boolean terminated = false;
    boolean inComment = false;
    Results.Errors errors = new Results.Errors();
    
    for (int i = index.incrementAndGet(); i < s.length(); i = index.incrementAndGet()) {

      char c = s.charAt(i);
      
      if (c == '#' && !inComment) {
        inComment = true;
      } else if (c == '\n') {
        inComment = false;
        line.incrementAndGet();
      } else if (inComment || Character.isWhitespace(c) || c == ',') {
        continue;
      } else if (c == '[') {
        Object converted = convert(s, index, context);
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
        } else if (!isHomogenousArray(converted, arrayItems)) {
          errors.heterogenous(context.identifier.getName(), line.get());
        } else {
          arrayItems.add(converted);
        }
        continue;
      } else if (c == ']') {
        terminated = true;
        break;
      } else {
        Object converted = CONVERTERS.convert(s, index, context);
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
        } else if (!isHomogenousArray(converted, arrayItems)) {
          errors.heterogenous(context.identifier.getName(), line.get());
        } else {
          arrayItems.add(converted);
        }
      }
    }
    
    if (!terminated) {
      errors.unterminated(context.identifier.getName(), s.substring(startIndex, s.length()), startLine);
    }
    
    if (errors.hasErrors()) {
      return errors;
    }
    
    return arrayItems;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.get(0).getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.get(0).getClass());
  }
  
  private ArrayConverter() {}
}
