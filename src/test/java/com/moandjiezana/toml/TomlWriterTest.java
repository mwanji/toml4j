package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("unused")
public class TomlWriterTest {

  @Rule
  public TemporaryFolder testDirectory = new TemporaryFolder();

  @Test
  public void should_write_primitive_types() {
    class TestClass {
      public String aString;
      int anInt;
      protected float aFloat;
      private double aDouble;
      boolean aBoolean;
      final int aFinalInt = 1; // Should be skipped
      Date aDate;
    }

    TestClass o = new TestClass();
    o.aString = "hello";
    o.anInt = 4;
    o.aFloat = 1.23f;
    o.aDouble = -5.43;
    o.aBoolean = false;

    o.aDate = new Date();
    String theDate = formatDate(o.aDate);

    String output = new TomlWriter().write(o);
    String expected = "aString = \"hello\"\n" +
        "anInt = 4\n" +
        "aFloat = 1.23\n" +
        "aDouble = -5.43\n" +
        "aBoolean = false\n" +
        "aDate = " + theDate + "\n";

    assertEquals(expected, output);
  }

  private String formatDate(Date date) {
    // Copying the date formatting code from DateValueWriter isn't optimal, but
    // I can't see any other way to check date formatting - the test gets
    // run in multiple time zones, so we can't just hard-code a time zone.
    String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:m:ss").format(date);
    Calendar calendar = new GregorianCalendar();
    int tzOffset = (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
    dateString += String.format("%+03d:%02d", tzOffset / 60, tzOffset % 60);

    return dateString;
  }

  class SubChild {
    int anInt;
  }
  class Child {
    SubChild subChild;
    int anInt;
  }
  class Parent {
    Map<String, Object> aMap;
    Child child;
    boolean aBoolean;
  }

  private Parent buildNestedMap() {
    Parent parent = new Parent();
    parent.aMap = new LinkedHashMap<String, Object>();
    parent.aMap.put("foo", 1);
    parent.aMap.put("bar", "value1");
    parent.aMap.put("baz.x", true);
    parent.child = new Child();
    parent.child.anInt = 2;
    parent.child.subChild = new SubChild();
    parent.child.subChild.anInt = 4;
    parent.aBoolean = true;

    return parent;
  }

  @Test
  public void should_write_nested_map_with_default_indentation_policy() {
    String output = new TomlWriter().write(buildNestedMap());
    String expected = "aBoolean = true\n\n" +
        "[aMap]\n" +
        "foo = 1\n" +
        "bar = \"value1\"\n" +
        "\"baz.x\" = true\n\n" +
        "[child]\n" +
        "anInt = 2\n\n" +
        "[child.subChild]\n" +
        "anInt = 4\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_follow_indentation_policy_of_indented_values() {
    String output = new TomlWriter().
        setIndentationPolicy(new WriterIndentationPolicy().setKeyValueIndent(2)).
        write(buildNestedMap());
    String expected = "aBoolean = true\n\n" +
        "[aMap]\n" +
        "  foo = 1\n" +
        "  bar = \"value1\"\n" +
        "  \"baz.x\" = true\n\n" +
        "[child]\n" +
        "  anInt = 2\n\n" +
        "[child.subChild]\n" +
        "  anInt = 4\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_follow_indentation_policy_of_indented_tables() {
    String output = new TomlWriter().
        setIndentationPolicy(new WriterIndentationPolicy().setTableIndent(2)).
        write(buildNestedMap());
    String expected = "aBoolean = true\n\n" +
        "[aMap]\n" +
        "foo = 1\n" +
        "bar = \"value1\"\n" +
        "\"baz.x\" = true\n\n" +
        "[child]\n" +
        "anInt = 2\n\n" +
        "  [child.subChild]\n" +
        "  anInt = 4\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_follow_indentation_policy_of_indented_tables_and_values() {
    String output = new TomlWriter().
        setIndentationPolicy(new WriterIndentationPolicy().setTableIndent(2).setKeyValueIndent(2)).
        write(buildNestedMap());
    String expected = "aBoolean = true\n\n" +
        "[aMap]\n" +
        "  foo = 1\n" +
        "  bar = \"value1\"\n" +
        "  \"baz.x\" = true\n\n" +
        "[child]\n" +
        "  anInt = 2\n\n" +
        "  [child.subChild]\n" +
        "    anInt = 4\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_write_array_of_tables() {
    class Table {
      int anInt;

      Table(int anInt) {
        this.anInt = anInt;
      }
    }
    class Config {
      Table[] table;
    }
    Config config = new Config();
    config.table = new Table[]{new Table(1), new Table(2)};

    String output = new TomlWriter().write(config);
    String expected = "[[table]]\n" +
        "anInt = 1\n\n" +
        "[[table]]\n" +
        "anInt = 2\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_write_array_of_array() {
    class ArrayTest {
      int[][] array = {{1, 2, 3}, {4, 5, 6}};
    }
    ArrayTest arrayTest = new ArrayTest();

    String output = new TomlWriter().write(arrayTest);
    String expected = "array = [ [ 1, 2, 3 ], [ 4, 5, 6 ] ]\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_write_list() {
    class ListTest {
      List<Integer> aList = new LinkedList<Integer>();
    }
    ListTest o = new ListTest();
    o.aList.add(1);
    o.aList.add(2);

    assertEquals("aList = [ 1, 2 ]\n", new TomlWriter().write(o));
  }

  @Test
  public void should_handle_zero_length_arrays_and_lists() {
    class TestClass {
      List<Integer> aList = new LinkedList<Integer>();
      Float[] anArray = new Float[0];
    }
    assertEquals("", new TomlWriter().write(new TestClass()));
  }

  @Test(expected = IllegalStateException.class)
  public void should_reject_heterogeneous_arrays() {
    class BadArray {
      Object[] array = new Object[2];
    }
    BadArray badArray = new BadArray();
    badArray.array[0] = new Integer(1);
    badArray.array[1] = "oops";

    new TomlWriter().write(badArray);
  }

  @Test
  public void should_elide_empty_intermediate_tables() {
    class C {
      int anInt = 1;
    }
    class B {
      C c = new C();
    }
    class A {
      B b = new B();
    }

    assertEquals("[b.c]\nanInt = 1\n", new TomlWriter().write(new A()));
  }

  class Base {
    protected int anInt = 2;
  }
  class Impl extends Base {
    boolean aBoolean = true;
  }

  @Test
  public void should_write_classes_with_inheritance() {
    Impl impl = new Impl();
    String expected = "aBoolean = true\nanInt = 2\n";
    assertEquals(expected, new TomlWriter().write(impl));
  }

  @Test
  public void should_write_strings_to_toml_utf8() throws UnsupportedEncodingException {
    String input = " é foo € \b \t \n \f \r \" \\ ";
    assertEquals("\" \\u00E9 foo \\u20AC \\b \\t \\n \\f \\r \\\" \\\\ \"", new TomlWriter().write(input));

    // Check unicode code points greater than 0XFFFF
    input = " \uD801\uDC28 \uD840\uDC0B ";
    assertEquals("\" \\U00010428 \\U0002000B \"", new TomlWriter().write(input));
  }

  @Test
  public void should_quote_keys() {
    Map<String, Integer> aMap = new LinkedHashMap<String, Integer>();
    aMap.put("a.b", 1);
    aMap.put("5€", 2);
    aMap.put("c$d", 3);
    aMap.put("e/f", 4);

    String expected = "\"a.b\" = 1\n" +
        "\"5€\" = 2\n" +
        "\"c$d\" = 3\n" +
        "\"e/f\" = 4\n";
    assertEquals(expected, new TomlWriter().write(aMap));
  }
  
  @Test
  public void should_handle_urls() throws Exception {
    class WithUrl {
      URL url;
      URI uri;
    }
    
    WithUrl from = new WithUrl();
    from.url = new URL("https://github.com");
    from.uri = new URI("https://bitbucket.com");
    
    String expected = "url = \"https://github.com\"\n"
      + "uri = \"https://bitbucket.com\"\n";
    
    assertEquals(expected, new TomlWriter().write(from));
  }

  @Test
  public void should_handle_enum() throws Exception {
    class WithEnum {
      RetentionPolicy retentionPolicy = RetentionPolicy.RUNTIME;
    }
    
    assertEquals("retentionPolicy = \"RUNTIME\"\n", new TomlWriter().write(new WithEnum()));
  }
  
  @Test
  public void should_handle_char() throws Exception {
    class WithChar {
      char c = 'a';
    }
    
    assertEquals("c = \"a\"\n", new TomlWriter().write(new WithChar()));
  }
  
  @Test
  public void should_handle_big_numbers() throws Exception {
    class WithBigNumbers {
      BigInteger bigInt = BigInteger.valueOf(1);
      BigDecimal bigDecimal = BigDecimal.valueOf(2.8);
    }
    
    String expected = "bigInt = 1\n"
      + "bigDecimal = 2.8\n";
    
    assertEquals(expected, new TomlWriter().write(new WithBigNumbers()));
  }
  
  @Test
  public void should_handle_wrappers() throws Exception {
    class WithWrappers {
      Character c = Character.valueOf('b');
      Long l = Long.valueOf(2);
      Double d = Double.valueOf(3.4);
    }
    
    String expected = "c = \"b\"\n"
      + "l = 2\n"
      + "d = 3.4\n";
    
    assertEquals(expected, new TomlWriter().write(new WithWrappers()));
  }
  
  private static class SimpleTestClass {
    int a = 1;
  }

  @Test
  public void should_write_to_writer() throws IOException {
    StringWriter output = new StringWriter();
    new TomlWriter().write(new SimpleTestClass(), output);

    assertEquals("a = 1\n", output.toString());
  }

  @Test
  public void should_write_to_outputstream() throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    new TomlWriter().write(new SimpleTestClass(), output);

    assertEquals("a = 1\n", output.toString());
  }

  @Test
  public void should_write_to_file() throws IOException {
    File output = testDirectory.newFile();
    new TomlWriter().write(new SimpleTestClass(), output);

    assertEquals("a = 1\n", readFile(output));
  }

  private String readFile(File input) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(input));

    try {
      StringBuilder w = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        w.append(line).append('\n');
        line = bufferedReader.readLine();
      }

      return w.toString();
    } finally {
      bufferedReader.close();
    }
  }
}
