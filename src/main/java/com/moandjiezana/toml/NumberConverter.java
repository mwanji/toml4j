package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

class NumberConverter implements ValueConverter {
  static final NumberConverter NUMBER_PARSER = new NumberConverter();
  
  @Override
  public boolean canConvert(String s) {
    char firstChar = s.charAt(0);
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
  }
  
  public static void main(String[] args) {
    new NumberConverter().convert("5e+22");
  }

  @Override
  public Object convert(String s) {
    char[] chars = s.toCharArray();
    boolean whitespace = false;
    boolean signable = true;
    boolean dottable = false;
    boolean exponentable = false;
    String type = "";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];

      if (Character.isDigit(c)) {
        sb.append(c);
        signable = false;
        if (type.isEmpty()) {
          type = "integer";
          dottable = true;
        }
        exponentable = !type.equals("exponent");
      } else if ((c == '+' || c == '-') && signable && chars.length > i + 1) {
        signable = false;
        if (c == '-') {
          sb.append('-');
        }
      } else if (c == '.' && dottable && chars.length > i + 1) {
        sb.append('.');
        type = "float";
        dottable = false;
        exponentable = false;
      } else if ((c == 'E' || c == 'e') && exponentable && chars.length > i + 1) {
        sb.append('E');
        type = "exponent";
        signable = true;
        dottable = false;
        exponentable = false;
      } else if (Character.isWhitespace(c)) {
        whitespace = true;
      } else if (whitespace && c == '#') {
        break;
      } else {
        type = "";
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
      return INVALID;
    }
  }
}
