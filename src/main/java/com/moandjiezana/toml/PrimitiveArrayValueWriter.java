package com.moandjiezana.toml;

import java.util.Collection;

import static com.moandjiezana.toml.ValueWriters.WRITERS;

class PrimitiveArrayValueWriter extends ArrayValueWriter {
  static final ValueWriter PRIMITIVE_ARRAY_VALUE_WRITER = new PrimitiveArrayValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return isArrayish(value) && isArrayOfPrimitive(value);
  }

  @Override
  public void write(Object value, WriterContext context) {
    Collection values = normalize(value);

    context.output.append('[');
    if (!context.getTomlWriter().wantTerseArrays()) {
      context.output.append(' ');
    }

    boolean first = true;
    ValueWriter firstWriter = null;

    for (Object elem : values) {
      if (first) {
        firstWriter = WRITERS.findWriterFor(elem);
        first = false;
      } else {
        ValueWriter writer = WRITERS.findWriterFor(elem);
        if (writer != firstWriter) {
          throw new IllegalStateException(
              context.getContextPath() +
                  ": cannot write a heterogeneous array; first element was of type " + firstWriter +
                  " but found " + writer
          );
        }
        context.output.append(", ");
      }

      WRITERS.write(elem, context);
    }

    if (!context.getTomlWriter().wantTerseArrays()) {
      context.output.append(' ');
    }
    context.output.append(']');
  }

  private PrimitiveArrayValueWriter() {}

  @Override
  public String toString() {
    return "primitive-array";
  }
}
