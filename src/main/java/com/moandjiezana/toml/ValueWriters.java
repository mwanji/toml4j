package com.moandjiezana.toml;

import static com.moandjiezana.toml.BooleanConverter.BOOLEAN_PARSER;
import static com.moandjiezana.toml.DateConverter.DATE_PARSER;
import static com.moandjiezana.toml.DateConverter.DATE_PARSER_JDK_6;
import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;
import static com.moandjiezana.toml.NumberConverter.NUMBER_PARSER;
import static com.moandjiezana.toml.ObjectValueWriter.OBJECT_VALUE_WRITER;
import static com.moandjiezana.toml.PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.StringConverter.STRING_PARSER;
import static com.moandjiezana.toml.TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER;

class ValueWriters {

  static final ValueWriters WRITERS = new ValueWriters();

  ValueWriter findWriterFor(Object value) {
    for (ValueWriter valueWriter : VALUE_WRITERs) {
      if (valueWriter.canWrite(value)) {
        return valueWriter;
      }
    }

    return OBJECT_VALUE_WRITER;
  }

  private ValueWriters() {}
  
  private static DateConverter getPlatformSpecificDateConverter() {
    return Runtime.class.getPackage().getSpecificationVersion().startsWith("1.6") ? DATE_PARSER_JDK_6 : DATE_PARSER;
  }

  private static final ValueWriter[] VALUE_WRITERs = {
      STRING_PARSER, NUMBER_PARSER, BOOLEAN_PARSER, getPlatformSpecificDateConverter(),
      MAP_VALUE_WRITER, PRIMITIVE_ARRAY_VALUE_WRITER, TABLE_ARRAY_VALUE_WRITER
  };
}
