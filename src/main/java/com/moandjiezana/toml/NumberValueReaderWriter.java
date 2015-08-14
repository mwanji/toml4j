package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class NumberValueReaderWriter implements ValueReader, ValueWriter {
  static final NumberValueReaderWriter NUMBER_VALUE_READER_WRITER = new NumberValueReaderWriter();
  
  @Override
  public boolean canRead(String s) {
    char firstChar = s.charAt(0);
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
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

  @Override
  public boolean canWrite(Object value) {
    return Number.class.isInstance(value);
  }

  @Override
  public void write(Object value, WriterContext context) {
    context.write(value.toString());
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
