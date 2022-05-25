package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class NumberValueReaderWriter implements ValueReader, ValueWriter {
  static final NumberValueReaderWriter NUMBER_VALUE_READER_WRITER = new NumberValueReaderWriter();

  @Override
  public boolean canRead(String s) {
    char firstChar = s.charAt(0);
    int dash = 0;

    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (c == '\n') {
        break;
      }
      else if (c == '-') {
        dash++;
      }
    }
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar) || firstChar == 'n' || firstChar == 'i';
  }

  @Override
  public Object read(String s, AtomicInteger index, Context context) {
    boolean signable = true;
    boolean dottable = false;
    boolean exponentable = false;
    boolean terminatable = false;
    boolean underscorable = false;
    String type = "";
    StringBuilder sb = new StringBuilder();

    for (int i = index.get(); i < s.length(); i = index.incrementAndGet()) {
      char c = s.charAt(i);
      boolean notLastChar = s.length() > i + 1;

      if (Character.isDigit(c)) {
        sb.append(c);
        signable = false;
        terminatable = true;
        if (type.isEmpty()) {
          type = "integer";
          dottable = true;
        }
        underscorable = notLastChar;
        exponentable = !type.equals("exponent");
      } else if ((c == '+' || c == '-') && signable && notLastChar) {
        signable = false;
        terminatable = false;
        if (c == '-') {
          sb.append('-');
        }
      } else if (c == '.' && dottable && notLastChar) {
        sb.append('.');
        type = "float";
        terminatable = false;
        dottable = false;
        exponentable = false;
        underscorable = false;
      } else if ((c == 'E' || c == 'e') && exponentable && notLastChar) {
        sb.append('E');
        type = "exponent";
        terminatable = false;
        signable = true;
        dottable = false;
        exponentable = false;
        underscorable = false;
      } else if (c == '_' && underscorable && notLastChar && Character.isDigit(s.charAt(i + 1))) {
        underscorable = false;
      } else if (c == 'n' || c == 'a' || c == 'i' || c == 'f') {
        sb.append(c);
        if (type.isEmpty()) {
          if (sb.length() == 3) {
            if (sb.toString().equals("nan")) {
              type = "nan";
              terminatable = true;
            } else if (sb.toString().equals("inf")) {
              type = "+inf";
              terminatable = true;
            }
          } else if (sb.length() == 4 && (sb.charAt(0) == '+' || sb.charAt(0) == '-')) {
            final String substring = sb.substring(1, 4);
            if (substring.equals("nan")) {
              type = "nan";
              terminatable = true;
            } else if (substring.equals("inf")) {
              type = sb.charAt(0) + "inf";
              terminatable = true;
            }
          }
        }
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
    } else if (type.equals("nan")) {
      return Double.NaN;
    } else if (type.equals("+inf")) {
      return Double.POSITIVE_INFINITY;
    } else if (type.equals("-inf")) {
      return Double.NEGATIVE_INFINITY;
    } else {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), sb.toString(), context.line.get());
      return errors;
    }
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof Number;
  }

  @Override
  public void write(Object value, WriterContext context) {
    if (value instanceof Double) {
      final double val = (double) value;
      if (Double.isFinite(val)) {
        context.write(val + "");
      }
      else if (Double.isNaN(val)) {
        context.write("nan");
      }
      else if (Double.POSITIVE_INFINITY == val){
        context.write("inf");
      }
      else {
        context.write("-inf");
      }
    } else if (value instanceof Float) {
      final float val = (float) value;
      if (Float.isFinite(val)) {
        context.write(val + "");
      }
      else if (Float.isNaN(val)) {
        context.write("nan");
      }
      else if (Float.POSITIVE_INFINITY == val){
        context.write("inf");
      }
      else {
        context.write("-inf");
      }
    } else {
      context.write(value.toString());
    }
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public String toString() {
    return "number";
  }
}
