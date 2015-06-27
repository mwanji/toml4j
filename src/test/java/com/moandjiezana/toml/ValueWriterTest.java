package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ValueWriterTest {

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

  @Test
  public void should_write_nested_map() {
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

    String output = new TomlWriter().write(parent);
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
  public void should_write_array_of_primitive() {
    class ArrayTest {
      int[] array = {1, 2, 3};
    }

    ArrayTest arrayTest = new ArrayTest();
    String output = new TomlWriter().write(arrayTest);
    String expected = "array = [ 1, 2, 3 ]\n";
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
        "  anInt = 1\n\n" +
        "[[table]]\n" +
        "  anInt = 2\n";
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

    assertEquals("[b.c]\n  anInt = 1\n", new TomlWriter().write(new A()));
  }

  @Test
  public void should_write_nested_arrays_of_tables() {
    class Physical {
      String color;
      String shape;
    }
    class Variety {
      String name;
    }
    class Fruit {
      Physical physical;
      Variety[] variety;
      String name;
    }
    class Basket {
      Fruit[] fruit;
    }

    Basket basket = new Basket();
    basket.fruit = new Fruit[2];

    basket.fruit[0] = new Fruit();
    basket.fruit[0].name = "apple";
    basket.fruit[0].physical = new Physical();
    basket.fruit[0].physical.color = "red";
    basket.fruit[0].physical.shape = "round";
    basket.fruit[0].variety = new Variety[2];
    basket.fruit[0].variety[0] = new Variety();
    basket.fruit[0].variety[0].name = "red delicious";
    basket.fruit[0].variety[1] = new Variety();
    basket.fruit[0].variety[1].name = "granny smith";

    basket.fruit[1] = new Fruit();
    basket.fruit[1].name = "banana";
    basket.fruit[1].variety = new Variety[1];
    basket.fruit[1].variety[0] = new Variety();
    basket.fruit[1].variety[0].name = "plantain";

    String expected = "[[fruit]]\n" +
        "  name = \"apple\"\n" +
        "\n" +
        "[fruit.physical]\n" +
        "  color = \"red\"\n" +
        "  shape = \"round\"\n" +
        "\n" +
        "[[fruit.variety]]\n" +
        "  name = \"red delicious\"\n" +
        "\n" +
        "[[fruit.variety]]\n" +
        "  name = \"granny smith\"\n" +
        "\n" +
        "[[fruit]]\n" +
        "  name = \"banana\"\n" +
        "\n" +
        "[[fruit.variety]]\n" +
        "  name = \"plantain\"" +
        "\n";


    String output = new TomlWriter().write(basket);
    assertEquals(expected, output);
  }

  @Test
  public void should_write_classes_with_inheritance() {
    class Parent {
      protected int anInt = 2;
    }
    class Child extends Parent {
      boolean aBoolean = true;
    }

    Child child = new Child();
    String expected = "aBoolean = true\nanInt = 2\n";
    assertEquals(expected, new TomlWriter().write(child));
  }

  @Test
  public void should_write_strings_to_toml_utf8() throws UnsupportedEncodingException {
    String input = " é foo € \b \t \n \f \r \" \\ ";
    assertEquals("\" \\u00E9 foo \\u20AC \\b \\t \\n \\f \\r \\\" \\ \"", new TomlWriter().write(input));

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

    StringBuilder w = new StringBuilder();
    String line = bufferedReader.readLine();
    while (line != null) {
      w.append(line).append('\n');
      line = bufferedReader.readLine();
    }

    return w.toString();
  }
}