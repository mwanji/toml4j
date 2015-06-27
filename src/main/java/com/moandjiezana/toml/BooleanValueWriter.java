package com.moandjiezana.toml;

class BooleanValueWriter implements ValueWriter {
  static final ValueWriter BOOLEAN_VALUE_WRITER = new BooleanValueWriter();

  @Override
  public boolean canWrite(Object value) {
    return Boolean.class.isInstance(value);
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
