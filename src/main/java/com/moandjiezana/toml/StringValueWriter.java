package com.moandjiezana.toml;

import java.net.URI;
import java.net.URL;

public class StringValueWriter implements ValueWriter {

  static final ValueWriter STRING_VALUE_WRITER = new StringValueWriter();
  
  static private final String[] specialCharacterEscapes = new String[93];

  static {
    specialCharacterEscapes['\b'] = "\\b";
    specialCharacterEscapes['\t'] = "\\t";
    specialCharacterEscapes['\n'] = "\\n";
    specialCharacterEscapes['\f'] = "\\f";
    specialCharacterEscapes['\r'] = "\\r";
    specialCharacterEscapes['"'] = "\\\"";
    specialCharacterEscapes['\\'] = "\\\\";
  }	
	
  @Override
  public boolean canWrite(Object value) {
    return value instanceof String || value instanceof Character || value instanceof URL || value instanceof URI || value instanceof Enum;
  }

  @Override
  public void write(Object value, WriterContext context) {
    String valueString = value.toString();
    if (context.getStringPolicy().isHandleMultiLineStrings() && containsSpecialCharacter(valueString)) {
      writeMultiLineString(valueString, context);
    } else {
      writeBasicString(valueString, context);
    }
  }

  private void writeMultiLineString(String valueString, WriterContext context) {
    context.write("\"\"\"\n");
    context.write(valueString);
    context.write("\"\"\"");
  }

  private void writeBasicString(String valueString, WriterContext context) {
    context.write('"');
	escapeUnicode(valueString, context);
    context.write('"');
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }
  
  private boolean containsSpecialCharacter(String in) {
    for (int i = 0; i < in.length(); i++) {
      int codePoint = in.codePointAt(i);
      if (codePoint < specialCharacterEscapes.length && specialCharacterEscapes[codePoint] != null) {
        return true;
      }
    }
    return false;
  }

  private void escapeUnicode(String in, WriterContext context) {
    for (int i = 0; i < in.length(); i++) {
      int codePoint = in.codePointAt(i);
      if (codePoint < specialCharacterEscapes.length && specialCharacterEscapes[codePoint] != null) {
        context.write(specialCharacterEscapes[codePoint]);
      } else {
        context.write(in.charAt(i));
      }
    }
  }
  
  private StringValueWriter() {}

}
