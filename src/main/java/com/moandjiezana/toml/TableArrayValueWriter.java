package com.moandjiezana.toml;

import java.util.Collection;

class TableArrayValueWriter extends ArrayValueWriter {
  static final ValueWriter TABLE_ARRAY_VALUE_WRITER = new TableArrayValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return isArrayish(value) && !isArrayOfPrimitive(value);
  }

  @Override
  public void write(Object value, WriterContext context) {
    Collection values = normalize(value);

    WriterContext subContext = context.extend().setIsArrayOfTable(true);

    for (Object elem : values) {
      ValueWriters.write(elem, subContext);
    }
  }
}
