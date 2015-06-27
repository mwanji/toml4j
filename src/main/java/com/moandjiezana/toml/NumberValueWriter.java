package com.moandjiezana.toml;

class NumberValueWriter implements ValueWriter {
  static final ValueWriter NUMBER_VALUE_WRITER = new NumberValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return Number.class.isInstance(value);
  }

  @Override
  public void write(Object value, WriterContext context) {
    context.output.append(value.toString());
  }

  @Override
  public boolean isPrimitiveType() {
    return true;
  }

  @Override
  public boolean isTable() {
    return false;
  }
}
