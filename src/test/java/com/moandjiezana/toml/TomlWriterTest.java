package com.moandjiezana.toml;

import static org.junit.Assert.*;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("unused")
public class TomlWriterTest {

  @Rule
  public TemporaryFolder testDirectory = new TemporaryFolder();
  
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void should_write_primitive_types() {
    class TestClass {
      public String aString = "hello";
      int anInt = 4;
      protected float aFloat = 1.23f;
      private double aDouble = -5.43;
      final boolean aBoolean = false;
      static final int aFinalInt = 1; // Should be skipped
      Date aDate;
    }

    TestClass o = new TestClass();

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Johannesburg"));
    calendar.set(2015, Calendar.JULY, 1, 11, 5, 30);
    calendar.set(Calendar.MILLISECOND, 0);
    o.aDate = calendar.getTime();
    
    String output = new TomlWriter().write(o);
    String expected = "aString = \"hello\"\n" +
        "anInt = 4\n" +
        "aFloat = 1.23\n" +
        "aDouble = -5.43\n" +
        "aBoolean = false\n" +
        "aDate = 2015-07-01T09:05:30Z\n";

    assertEquals(expected, output);
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
    String output = new TomlWriter.Builder().
      indentValuesBy(2).
      build().
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
    String output = new TomlWriter.Builder().
      indentTablesBy(2).
      build().
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
    String output = new TomlWriter.Builder().
      indentValuesBy(2).
      indentTablesBy(2).
      build().
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
  public void should_write_array_of_tables_from_object() {
    class Table {
      int anInt;

      Table(int anInt) {
        this.anInt = anInt;
      }
    }
    class Config {
      Table[] table = new Table[]{new Table(1), new Table(2)};
      List<Table> table2 = Arrays.asList(new Table(3), new Table(4));
    }
    Config config = new Config();

    String output = new TomlWriter().write(config);
    String expected = "[[table]]\n" +
        "anInt = 1\n\n" +
        "[[table]]\n" +
        "anInt = 2\n\n" +
        "[[table2]]\n" +
        "anInt = 3\n\n" +
        "[[table2]]\n" +
        "anInt = 4\n";
    assertEquals(expected, output);
  }
  
  @Test
  public void should_write_array_of_tables_from_map() throws Exception {
    List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
    
    HashMap<String, Object> item1 = new HashMap<String, Object>();
    item1.put("anInt", 1L);
    HashMap<String, Object> item2 = new HashMap<String, Object>();
    item2.put("anInt", 2L);

    maps.add(item1);
    maps.add(item2);
    
    Map<String, Object> input = new HashMap<String, Object>();
    input.put("maps", maps);
    
    String output = new TomlWriter().write(input);
    
    String expected = "[[maps]]\n" +
        "anInt = 1\n\n" +
        "[[maps]]\n" +
        "anInt = 2\n";
    assertEquals(expected, output);
  }

  @Test
  public void should_write_array_of_array() {
    class ArrayTest {
      int[][] array = {{1, 2, 3}, {4, 5, 6}};
    }
    ArrayTest arrayTest = new ArrayTest();

    String output = new TomlWriter.Builder().padArrayDelimitersBy(1).build().write(arrayTest);
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

    assertEquals("aList = [ 1, 2 ]\n", new TomlWriter.Builder().padArrayDelimitersBy(1).build().write(o));
  }

  @Test
  public void should_handle_zero_length_arrays_and_lists() {
    class TestClass {
      List<Integer> aList = new LinkedList<Integer>();
      Float[] anArray = new Float[0];
    }
    assertEquals("aList = []\nanArray = []\n", new TomlWriter().write(new TestClass()));
  }

  @Test
  public void should_reject_heterogeneous_arrays() {
    class BadArray {
      Object[] array = new Object[2];
    }
    BadArray badArray = new BadArray();
    badArray.array[0] = new Integer(1);
    badArray.array[1] = "oops";

    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage(Matchers.startsWith("array"));
    
    new TomlWriter().write(badArray);
  }

  @Test
  public void should_reject_nested_heterogeneous_array() {
    class BadArray {
      Map<String, Object> aMap = new HashMap<String, Object>();
    }
    
    BadArray badArray = new BadArray();
    badArray.aMap.put("array", new Object[] { Integer.valueOf(1), "oops" });

    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("aMap.array");

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

  @Test
  public void should_write_map() throws Exception {
    assertEquals("a = 1\n", new TomlWriter().write(new Toml().read("a = 1").toMap()));
  }

  @Test
  public void should_write_object_map() {
    HashMap<String, Object> items = new HashMap();

    HashMap<String, Object> item1 = new HashMap();
    item1.put("id", "1");
    item1.put("display", "Foo");

    HashMap<String, Object> item2 = new HashMap();
    item2.put("id", "2");
    item2.put("display", "Bar");

    HashMap<String, Object> item3 = new HashMap();
    item3.put("id", "3");
    item3.put("display", "Moo");

    items.put("foo", item1);
    items.put("bar", item2);
    items.put("moo", item3);

    String toml = new TomlWriter().write(items);
    assertEquals("[bar]\n" +
      "display = \"Bar\"\n" +
      "id = \"2\"\n" +
      "\n" +
      "[foo]\n" +
      "display = \"Foo\"\n" +
      "id = \"1\"\n" +
      "\n" +
      "[moo]\n" +
      "display = \"Moo\"\n" +
      "id = \"3\"\n", toml);
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
    class Utf8Test {
      String input;
    }

    Utf8Test utf8Test = new Utf8Test();
    utf8Test.input = " é foo \u20AC \b \t \n \f \r \" \\ ";
    assertEquals("input = \" é foo € \\b \\t \\n \\f \\r \\\" \\\\ \"\n", new TomlWriter().write(utf8Test));

    // Check unicode code points greater than 0XFFFF
    utf8Test.input = " \uD801\uDC28 \uD840\uDC0B ";
    assertEquals("input = \" 𐐨 𠀋 \"\n", new TomlWriter().write(utf8Test));
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
  public void should_quote_keys_in_object() throws Exception {
    class A$ {
      Double µµ = 5.3;
    }
    
    class A {
      int €5 = 5;
      String français = "langue";
      A$ a$ = new A$();
    }
    
    assertEquals("\"€5\" = 5\n\"français\" = \"langue\"\n\n[\"a$\"]\n\"µµ\" = 5.3\n", new TomlWriter().write(new A()));
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
  
  @Test
  public void should_use_specified_time_zone() throws Exception {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(2015, Calendar.JULY, 1, 11, 5, 30);
    calendar.set(Calendar.MILLISECOND, 0);
    
    Map<String, Date> o = new HashMap<String, Date>();
    
    o.put("sast", calendar.getTime());
    
    TomlWriter writer = new TomlWriter.Builder().
      timeZone(TimeZone.getTimeZone("Africa/Johannesburg")).
      build();
    
    assertEquals("sast = 2015-07-01T13:05:30+02:00\n", writer.write(o));
  }
  
  @Test
  public void should_show_fractional_seconds() throws Exception {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(2015, Calendar.JULY, 1, 11, 5, 30);
    calendar.set(Calendar.MILLISECOND, 345);
    
    Map<String, Date> o = new HashMap<String, Date>();
    
    o.put("date", calendar.getTime());
    
    TomlWriter writer = new TomlWriter.Builder().
      showFractionalSeconds().
      build();
    
    assertEquals("date = 2015-07-01T11:05:30.345Z\n", writer.write(o));
  }
  
  @Test
  public void should_show_fractional_seconds_in_specified_time_zone() throws Exception {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(2015, Calendar.JULY, 1, 11, 5, 30);
    calendar.set(Calendar.MILLISECOND, 345);
    
    Map<String, Date> o = new LinkedHashMap<String, Date>();
    
    o.put("date", calendar.getTime());
    calendar.set(Calendar.MINUTE, 37);
    o.put("date2", calendar.getTime());
    
    TomlWriter writer = new TomlWriter.Builder().
      timeZone(TimeZone.getTimeZone("Africa/Johannesburg")).
      showFractionalSeconds().
      build();
    
    String expected = "date = 2015-07-01T13:05:30.345+02:00\n"
      + "date2 = 2015-07-01T13:37:30.345+02:00\n";
    
    assertEquals(expected, writer.write(o));
  }

  private static class SimpleTestClass {
    int a = 1;
  }
  
  private static class TransientClass {
    int a = 2;
    transient int b = 3;
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
  
  @Test
  public void should_skip_transient_fields() throws Exception {
    String toml = new TomlWriter().write(new TransientClass());
    
    assertEquals("a = 2\n", toml);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_string_fragment() {
    new TomlWriter().write("fragment");
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_boolean_fragment() {
    new TomlWriter().write(true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_number_fragment() {
    new TomlWriter().write(42);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_date_fragment() {
    new TomlWriter().write(new Date());
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_array_fragment() {
    new TomlWriter().write(new int[2]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_refuse_to_write_table_array_fragment() {
    new TomlWriter().write(new SimpleTestClass[2]);
  }

  @Test(expected=IllegalArgumentException.class)
  public void should_not_write_list() throws Exception {
    new TomlWriter().write(Arrays.asList("a"));
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
