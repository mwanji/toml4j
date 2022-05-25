package com.moandjiezana.toml;

import static com.moandjiezana.toml.BooleanValueReaderWriter.BOOLEAN_VALUE_READER_WRITER;
import static com.moandjiezana.toml.LocalDateTimeValueReaderWriter.LOCAL_DATE_TIME_VALUE_READER_WRITER;
import static com.moandjiezana.toml.LocalDateValueReaderWriter.LOCAL_DATE_VALUE_READER_WRITER;
import static com.moandjiezana.toml.LocalTimeValueReaderWriter.LOCAL_TIME_VALUE_READER_WRITER;
import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;
import static com.moandjiezana.toml.NumberValueReaderWriter.NUMBER_VALUE_READER_WRITER;
import static com.moandjiezana.toml.ObjectValueWriter.OBJECT_VALUE_WRITER;
import static com.moandjiezana.toml.OffsetDateTimeValueReaderWriter.OFFSET_DATE_TIME_VALUE_READER_WRITER;
import static com.moandjiezana.toml.PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER;
import static com.moandjiezana.toml.StringValueReaderWriter.STRING_VALUE_READER_WRITER;
import static com.moandjiezana.toml.TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER;

class ValueWriters {

  static final ValueWriters WRITERS = new ValueWriters();

  ValueWriter findWriterFor(Object value) {
    for (ValueWriter valueWriter : VALUE_WRITERS) {
      if (valueWriter.canWrite(value)) {
        return valueWriter;
      }
    }

    return OBJECT_VALUE_WRITER;
  }

  private ValueWriters() {}

  private static final ValueWriter[] VALUE_WRITERS = {
          STRING_VALUE_READER_WRITER,
          NUMBER_VALUE_READER_WRITER,
          BOOLEAN_VALUE_READER_WRITER,
          LOCAL_TIME_VALUE_READER_WRITER,
          LOCAL_DATE_VALUE_READER_WRITER,
          LOCAL_DATE_TIME_VALUE_READER_WRITER,
          OFFSET_DATE_TIME_VALUE_READER_WRITER,
          MAP_VALUE_WRITER,
          PRIMITIVE_ARRAY_VALUE_WRITER,
          TABLE_ARRAY_VALUE_WRITER
  };
}
