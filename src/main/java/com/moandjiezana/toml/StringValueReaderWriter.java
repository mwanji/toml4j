package com.moandjiezana.toml;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

class StringValueReaderWriter implements ValueReader, ValueWriter {

  static final StringValueReaderWriter STRING_VALUE_READER_WRITER = new StringValueReaderWriter();

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
  public boolean canRead(String s) {
    return s.startsWith("\"");
  }

  @Override
  public Object read(String s, AtomicInteger index, Context context) {
    int startIndex = index.incrementAndGet();
    int endIndex = -1;

    for (int i = index.get(); i < s.length(); i = index.incrementAndGet()) {
      char ch = s.charAt(i);
      if (ch == '"' && s.charAt(i - 1) != '\\') {
        endIndex = i;
        break;
      }
    }

    if (endIndex == -1) {
      Results.Errors errors = new Results.Errors();
      errors.unterminated(context.identifier.getName(), s.substring(startIndex - 1),
          context.line.get());
      return errors;
    }

    String raw = s.substring(startIndex, endIndex);
    s = replaceUnicodeCharacters(raw);
    s = replaceSpecialCharacters(s);

    if (s == null) {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), raw, context.line.get());
      return errors;
    }

    return s;
  }

  String replaceUnicodeCharacters(String value) {
    if (value.length() < 6) {
      return value;
    }

    StringBuilder builder = new StringBuilder();
    int i;
    for (i = 0; i < value.length() - 1; ) {
      i += replaceUnicode(value, i, builder);
    }

    replaceUnicode(value, i, builder);

    return builder.toString();
  }

  int replaceUnicode(String value, int index, StringBuilder builder) {
    if (index >= value.length()) {
      return 0;
    }

    boolean eof = index >= value.length() - 2;

    char ch = value.charAt(index);
    if (!eof && ch == '\\' && (value.charAt(index + 1) == 'u' || value.charAt(index + 1) == 'U')) {
      if (index + 5 >= value.length()) {
        builder.append(ch);
        return 1;
      }

      String unicode = value.substring(index + 2, index + 6);
      builder.append(Character.toChars(Integer.parseInt(unicode, 16)));

      return 6;
    } else {
      if (ch == '\\' && value.charAt(index + 1) == '\\') {
        builder.append(ch);
        builder.append(ch);
        return 2;
      } else {
        builder.append(ch);
        return 1;
      }
    }
  }

  String replaceSpecialCharacters(String s) {
    for (int i = 0; i < s.length() - 1; i++) {
      char ch = s.charAt(i);
      char next = s.charAt(i + 1);

      if (ch == '\\' && next == '\\') {
        i++;
      } else if (ch == '\\' && !(next == 'b' || next == 'f' || next == 'n' || next == 't'
          || next == 'r' || next == '"' || next == '\\')) {
        return null;
      }
    }

    return s.replace("\\n", "\n")
        .replace("\\\"", "\"")
        .replace("\\t", "\t")
        .replace("\\r", "\r")
        .replace("\\\\", "\\")
        .replace("\\/", "/")
        .replace("\\b", "\b")
        .replace("\\f", "\f");
  }

  @Override
  public boolean canWrite(Object value) {
    return value instanceof String || value instanceof Character || value instanceof URL
        || value instanceof URI || value instanceof Enum;
  }

  @Override
  public void write(Object value, WriterContext context) {
    context.write('"');
    escapeUnicode(value.toString(), context);
    context.write('"');
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  private void escapeUnicode(String in, WriterContext context) {
    for (int i = 0; i < in.length(); i++) {
      int codePoint = in.codePointAt(i);
      if (codePoint < specialCharacterEscapes.length
          && specialCharacterEscapes[codePoint] != null) {
        context.write(specialCharacterEscapes[codePoint]);
      } else {
        context.write(in.charAt(i));
      }
    }
  }

  private StringValueReaderWriter() {
  }

  @Override
  public String toString() {
    return "string";
  }
}
