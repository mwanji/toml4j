package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueWriters.WRITERS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>Converts Objects to TOML</p>
 *
 * <p>An input Object can comprise arbitrarily nested combinations of Java primitive types,
 * other {@link Object}s, {@link Map}s, {@link List}s, and Arrays. {@link Object}s and {@link Map}s
 * are output to TOML tables, and {@link List}s and Array to TOML arrays.</p>
 *
 * <p>Example usage:</p>
 * <pre><code>
 * class AClass {
 *   int anInt = 1;
 *   int[] anArray = { 2, 3 };
 * }
 *
 * String tomlString = new TomlWriter().write(new AClass());
 * </code></pre>
 */
public class TomlWriter {

  private WriterIndentationPolicy indentationPolicy = new WriterIndentationPolicy();
  private boolean wantTerseArraysValue = false;
  private GregorianCalendar calendar = new GregorianCalendar();
  private DateFormat customDateFormat = null;

  /**
   * Creates a TomlWriter instance.
   */
  public TomlWriter() {}

  /**
   * Write an Object into TOML String.
   *
   * @param from the object to be written
   * @return a string containing the TOML representation of the given Object
   */
  public String write(Object from) {
    try {
      StringWriter output = new StringWriter();
      write(from, output);
      
      return output.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Write an Object in TOML to a {@link OutputStream}.
   *
   * @param from the object to be written
   * @param target the OutputStream to which the TOML will be written. The stream is not closed after being written to.
   * @throws IOException if target.write() fails
   */
  public void write(Object from, OutputStream target) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(target);
    write(from, writer);
    writer.flush();
  }

  /**
   * Write an Object in TOML to a {@link File}.
   *
   * @param from the object to be written
   * @param target the File to which the TOML will be written
   * @throws IOException if any file operations fail
   */
  public void write(Object from, File target) throws IOException {
    FileWriter writer = new FileWriter(target);
    write(from, writer);
    writer.close();
  }

  /**
   * Write an Object in TOML to a {@link Writer}.
   *
   * @param from the object to be written
   * @param target the Writer to which TOML will be written. The Writer is not closed.
   * @throws IOException if target.write() fails
   */
  public void write(Object from, Writer target) throws IOException {
    WRITERS.write(from, this, target);
  }

  public WriterIndentationPolicy getIndentationPolicy() {
    return indentationPolicy;
  }

  /**
   * Set the {@link WriterIndentationPolicy} for this writer.
   *
   * If unset, the default policy (no indentation) is used.
   *
   * @param indentationPolicy the new policy
   * @return this TomlWriter instance
   */
  public TomlWriter setIndentationPolicy(WriterIndentationPolicy indentationPolicy) {
    this.indentationPolicy = indentationPolicy;
    return this;
  }

  /**
   * <p>Control whitespace in arrays in the TOML output.</p>
   *
   * <p>Terse arrays = false (default):</p>
   *
   * <pre><code>
   *   a = [ 1, 2, 3 ]
   * </code></pre>
   *
   * <p>Terse arrays = true:</p>
   *
   * <pre><code>
   *   a = [1,2,3]
   * </code></pre>
   *
   * @param value terse arrays setting
   * @return this TomlWriter instance
   */
  public TomlWriter wantTerseArrays(boolean value) {
    this.wantTerseArraysValue = value;
    return this;
  }

  /**
   * Get the current array whitespace policy
   * @return the current policy
   */
  public boolean wantTerseArrays() {
    return wantTerseArraysValue;
  }

  /**
   * Set the {@link TimeZone} used when formatting datetimes.
   *
   * If unset, datetimes will be rendered in the current time zone.
   *
   * @param timeZone custom TimeZone.
   * @return this TomlWriter instance
   */
  public TomlWriter setTimeZone(TimeZone timeZone) {
    calendar = new GregorianCalendar(timeZone);
    return this;
  }

  /**
   * Get the {@link TimeZone} in use for this TomlWriter.
   *
   * @return the currently set TimeZone.
   */
  public TimeZone getTimeZone() {
    return calendar.getTimeZone();
  }

  /**
   * Override the default date format.
   *
   * If a time zone was set with {@link #setTimeZone(TimeZone)}, it will be applied before formatting
   * datetimes.
   *
   * @param customDateFormat a custom DateFormat
   * @return this TomlWriter instance
   */
  public TomlWriter setDateFormat(DateFormat customDateFormat) {
    this.customDateFormat = customDateFormat;
    return this;
  }

  public DateFormat getDateFormat() {
    return customDateFormat;
  }

  GregorianCalendar getCalendar() {
    return calendar;
  }
}
