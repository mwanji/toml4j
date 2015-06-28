package com.moandjiezana.toml;

import java.io.*;
import java.util.List;
import java.util.Map;

import static com.moandjiezana.toml.ValueWriters.WRITERS;

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
    return WRITERS.write(from, this);
  }

  /**
   * Write an Object in TOML to a {@link Writer}.
   *
   * @param from the object to be written
   * @param target the Writer to which TOML will be written
   * @throws IOException if target.write() fails
   */
  public void write(Object from, Writer target) throws IOException {
    target.write(write(from));
  }

  /**
   * Write an Object in TOML to a {@link OutputStream}.
   *
   * @param from the object to be written
   * @param target the OutputStream to which the TOML will be written
   * @throws IOException if target.write() fails
   */
  public void write(Object from, OutputStream target) throws IOException {
    target.write(write(from).getBytes());
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
    writer.write(write(from));
    writer.close();
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
}
