package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class NumberConverter implements ValueConverter {
  static final NumberConverter NUMBER_PARSER = new NumberConverter();
  
  @Override
  public boolean canConvert(String s) {
    char firstChar = s.charAt(0);
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
  }

  @Override
  public Object convert(String s, AtomicInteger index, Context context) {
    char[] chars = s.toCharArray();
    boolean signable = true;
    boolean dottable = false;
    boolean exponentable = false;
    boolean terminatable = false;
    String type = "";
    StringBuilder sb = new StringBuilder();

    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];

      if (Character.isDigit(c)) {
        sb.append(c);
        signable = false;
        terminatable = true;
        if (type.isEmpty()) {
          type = "integer";
          dottable = true;
        }
        exponentable = !type.equals("exponent");
      } else if ((c == '+' || c == '-') && signable && chars.length > i + 1) {
        signable = false;
        terminatable = false;
        if (c == '-') {
          sb.append('-');
        }
      } else if (c == '.' && dottable && chars.length > i + 1) {
        sb.append('.');
        type = "float";
        terminatable = false;
        dottable = false;
        exponentable = false;
      } else if ((c == 'E' || c == 'e') && exponentable && chars.length > i + 1) {
        sb.append('E');
        type = "exponent";
        terminatable = false;
        signable = true;
        dottable = false;
        exponentable = false;
      } else {
        if (!terminatable) {
          type = "";
        }
        index.decrementAndGet();
        break;
      }
    }

    if (type.equals("integer")) {
      return Long.valueOf(sb.toString());
    } else if (type.equals("float")) {
      return Double.valueOf(sb.toString());
    } else if (type.equals("exponent")) {
      String[] exponentString = sb.toString().split("E");
      
      return Double.parseDouble(exponentString[0]) * Math.pow(10, Double.parseDouble(exponentString[1]));
    } else {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), sb.toString(), context.line.get());
      return errors;
    }
  }
}
