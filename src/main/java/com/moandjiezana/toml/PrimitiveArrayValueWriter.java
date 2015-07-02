package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueWriters.WRITERS;

import java.util.Collection;

class PrimitiveArrayValueWriter extends ArrayValueWriter {
  static final ValueWriter PRIMITIVE_ARRAY_VALUE_WRITER = new PrimitiveArrayValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return isArrayish(value) && isArrayOfPrimitive(value);
  }

  @Override
  public void write(Object o, WriterContext context) {
    Collection<?> values = normalize(o);

    context.write('[');
    context.writeArrayDelimiterPadding();

    boolean first = true;
    ValueWriter firstWriter = null;

    for (Object value : values) {
      if (first) {
        firstWriter = WRITERS.findWriterFor(value);
        first = false;
      } else {
        ValueWriter writer = WRITERS.findWriterFor(value);
        if (writer != firstWriter) {
          throw new IllegalStateException(
              context.getContextPath() +
                  ": cannot write a heterogeneous array; first element was of type " + firstWriter +
                  " but found " + writer
          );
        }
        context.write(", ");
      }

      WRITERS.findWriterFor(value).write(value, context);
    }

    context.writeArrayDelimiterPadding();
    context.write(']');
  }

  private PrimitiveArrayValueWriter() {}

  @Override
  public String toString() {
    return "primitive-array";
  }
}
