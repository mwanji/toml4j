package com.moandjiezana.toml;

import java.util.Collection;

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
    if (context.getTomlWriter().wantSpaceyArrays()) {
      context.output.append(' ');
    }

    boolean first = true;
    for (Object elem : values) {
      if (!first) {
        context.output.append(", ");
      }
      ValueWriters.WRITERS.write(elem, context);
      first = false;
    }

    if (context.getTomlWriter().wantSpaceyArrays()) {
      context.output.append(' ');
    }
    context.output.append(']');
  }

  private PrimitiveArrayValueWriter() {}
}
