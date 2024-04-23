package com.moandjiezana.toml;

import static com.moandjiezana.toml.MapValueWriter.MAP_VALUE_WRITER;
import static com.moandjiezana.toml.ObjectValueWriter.OBJECT_VALUE_WRITER;
import static com.moandjiezana.toml.ValueWriters.WRITERS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
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
  
  public static class Builder {
    private int keyIndentation;
    private int tableIndentation;
    private int arrayDelimiterPadding = 0;
    private TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private boolean showFractionalSeconds = false;
    private boolean handleMultiLineStrings = false;
    
    public TomlWriter.Builder indentValuesBy(int spaces) {
      this.keyIndentation = spaces;
      
      return this;
    }

    public TomlWriter.Builder indentTablesBy(int spaces) {
      this.tableIndentation = spaces;
      
      return this;
    }
    
    public TomlWriter.Builder timeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
      
      return this;
    }
    
    /**
     * @param spaces number of spaces to put between opening square bracket and first item and between closing square bracket and last item
     * @return this TomlWriter.Builder instance
     */
    public TomlWriter.Builder padArrayDelimitersBy(int spaces) {
      this.arrayDelimiterPadding = spaces;
      
      return this;
    }
    
    public TomlWriter.Builder showFractionalSeconds() {
      this.showFractionalSeconds = true;
      return this;
    }
    
    public TomlWriter.Builder handleMultiLineStrings() {
      this.handleMultiLineStrings = true;
      return this;
    }
    
    public TomlWriter build() {
      return new TomlWriter(keyIndentation, tableIndentation, arrayDelimiterPadding, timeZone, showFractionalSeconds, handleMultiLineStrings);
    }
  }

  private final IndentationPolicy indentationPolicy;
  private final DatePolicy datePolicy;
  private final StringPolicy stringPolicy;

  /**
   * Creates a TomlWriter instance.
   */
  public TomlWriter() {
    this(0, 0, 0, TimeZone.getTimeZone("UTC"), false, false);
  }
  
  private TomlWriter(int keyIndentation, int tableIndentation, int arrayDelimiterPadding, TimeZone timeZone, boolean showFractionalSeconds, boolean handleMultiLineStrings) {
    this.indentationPolicy = new IndentationPolicy(keyIndentation, tableIndentation, arrayDelimiterPadding);
    this.datePolicy = new DatePolicy(timeZone, showFractionalSeconds);
    this.stringPolicy = new StringPolicy(handleMultiLineStrings);
  }

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
   * Write an Object in TOML to a {@link File}. Output is encoded as UTF-8.
   *
   * @param from the object to be written
   * @param target the File to which the TOML will be written
   * @throws IOException if any file operations fail
   */
  public void write(Object from, File target) throws IOException {
    OutputStream outputStream = new FileOutputStream(target);
    try {
      write(from, outputStream);
    } finally {
      outputStream.close();
    }
  }

  /**
   * Write an Object in TOML to a {@link OutputStream}. Output is encoded as UTF-8.
   *
   * @param from the object to be written
   * @param target the OutputStream to which the TOML will be written. The stream is NOT closed after being written to.
   * @throws IOException if target.write() fails
   */
  public void write(Object from, OutputStream target) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter(target, "UTF-8");
    write(from, writer);
    writer.flush();
  }

  /**
   * Write an Object in TOML to a {@link Writer}. You MUST ensure that the {@link Writer}s's encoding is set to UTF-8 for the TOML to be valid.
   *
   * @param from the object to be written. Can be a Map or a custom type. Must not be null.
   * @param target the Writer to which TOML will be written. The Writer is not closed.
   * @throws IOException if target.write() fails
   * @throws IllegalArgumentException if from is of an invalid type
   */
  public void write(Object from, Writer target) throws IOException {
    ValueWriter valueWriter = WRITERS.findWriterFor(from);
    if (valueWriter == MAP_VALUE_WRITER || valueWriter == OBJECT_VALUE_WRITER) {
      WriterContext context = new WriterContext(indentationPolicy, datePolicy, stringPolicy, target);
      valueWriter.write(from, context);
    } else {
      throw new IllegalArgumentException("An object of class " + from.getClass().getSimpleName() + " cannot produce valid TOML. Please pass in a Map or a custom type.");
    }
  }
}
