package com.moandjiezana.toml;

class StringSerializer implements Serializer {
  static final Serializer STRING_SERIALIZER = new StringSerializer();

  static private final String[] specialCharacterEscapes = new String[93];

  static {
    specialCharacterEscapes[0x08] = "\\b";
    specialCharacterEscapes[0x09] = "\\t";
    specialCharacterEscapes[0x0A] = "\\n";
    specialCharacterEscapes[0x0C] = "\\f";
    specialCharacterEscapes[0x0D] = "\\r";
    specialCharacterEscapes[0x22] = "\\\"";
    specialCharacterEscapes[0x5C] = "\\";
  }

  @Override
  public boolean canSerialize(Object value) {
    return value.getClass().isAssignableFrom(String.class);
  }

  @Override
  public void serialize(Object value, SerializerContext context) {
    context.serialized.append('"');
    escapeUnicode(value.toString(), context.serialized);
    context.serialized.append('"');
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public boolean isTable() {
    return false;
  }

  private void escapeUnicode(String in, StringBuilder out) {
    for (int i = 0; i < in.length(); i++) {
      int codePoint = in.codePointAt(i);
      if (codePoint < specialCharacterEscapes.length && specialCharacterEscapes[codePoint] != null) {
        out.append(specialCharacterEscapes[codePoint]);
      } else if (codePoint > 0x1f && codePoint < 0x7f) {
        out.append(Character.toChars(codePoint));
      } else if (codePoint <= 0xFFFF) {
        out.append(String.format("\\u%04X", codePoint));
      } else {
        out.append(String.format("\\U%08X", codePoint));
        // Skip the low surrogate, which will be the next in the code point sequence.
        i++;
      }
    }
  }
}
