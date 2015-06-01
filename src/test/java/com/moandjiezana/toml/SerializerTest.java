package com.moandjiezana.toml;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SerializerTest {
  @Test
  public void serializesPrimitiveTypes() {
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

    String theDate = "2015-05-31T08:44:03-07:00";
    Toml dateToml = new Toml().parse("a_date = " + theDate);
    o.aDate = dateToml.getDate("a_date");

    String serialized = Toml.serializeFrom(o);
    String expected = "aString = \"hello\"\n" +
        "anInt = 4\n" +
        "aFloat = 1.23\n" +
        "aDouble = -5.43\n" +
        "aBoolean = false\n" +
        "aDate = " + theDate + "\n";

    assertEquals(expected, serialized);
  }

  @Test
  public void serializesNestedMap() {
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

    String serialized = Toml.serializeFrom(parent);
    String expected = "aBoolean = true\n\n" +
        "[aMap]\n" +
        "  foo = 1\n" +
        "  bar = \"value1\"\n" +
        "  \"baz.x\" = true\n\n" +
        "[child]\n" +
        "  anInt = 2\n\n" +
        "[child.subChild]\n" +
        "  anInt = 4\n";
    assertEquals(expected, serialized);
  }

  @Test
  public void serializesArrayOfPrimitive() {
    class ArrayTest {
      int[] array = {1, 2, 3};
    }

    ArrayTest arrayTest = new ArrayTest();
    String serialized = Toml.serializeFrom(arrayTest);
    String expected = "array = [ 1, 2, 3 ]\n";
    assertEquals(expected, serialized);
  }

  @Test
  public void serializesArrayOfTables() {
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

    String serialized = Toml.serializeFrom(config);
    String expected = "[[table]]\n" +
        "  anInt = 1\n\n" +
        "[[table]]\n" +
        "  anInt = 2\n";
    assertEquals(expected, serialized);
  }

  @Test
  public void serializesArrayOfArray() {
    class ArrayTest {
      int[][] array = {{1, 2, 3}, {4, 5, 6}};
    }
    ArrayTest arrayTest = new ArrayTest();

    String serialized = Toml.serializeFrom(arrayTest);
    String expected = "array = [ [ 1, 2, 3 ], [ 4, 5, 6 ] ]\n";
    assertEquals(expected, serialized);
  }

  @Test
  public void serializesList() {
    class ListTest {
      List<Integer> aList = new LinkedList<Integer>();
    }
    ListTest o = new ListTest();
    o.aList.add(1);
    o.aList.add(2);

    assertEquals("aList = [ 1, 2 ]\n", Toml.serializeFrom(o));
  }

  @Test
  public void handlesZeroLengthArraysAndLists() {
    class TestClass {
      List<Integer> aList = new LinkedList<Integer>();
      Float[] anArray = new Float[0];
    }
    assertEquals("", Toml.serializeFrom(new TestClass()));
  }

  @Test
  public void elidesEmptyIntermediateTables() {
    class C {
      int anInt = 1;
    }
    class B {
      C c = new C();
    }
    class A {
      B b = new B();
    }

    assertEquals("[b.c]\n  anInt = 1\n", Toml.serializeFrom(new A()));
  }

  @Test
  public void serializesNestedArraysOfTables() {
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


    String serialized = Toml.serializeFrom(basket);
    assertEquals(expected, serialized);
  }

  @Test
  public void serializesClassesWithInheritance() {
    class Parent {
      protected int anInt = 2;
    }
    class Child extends Parent {
      boolean aBoolean = true;
    }

    Child child = new Child();
    String expected = "aBoolean = true\nanInt = 2\n";
    assertEquals(expected, Toml.serializeFrom(child));
  }

  @Test
  public void emptyTomlSerializesToEmptyString() {
    Toml toml = new Toml();
    assertEquals("", toml.serialize());
  }

  @Test
  public void serializesStringsToTomlUtf8() throws UnsupportedEncodingException {
    String input = " é foo € \b \t \n \f \r \" \\ ";
    assertEquals("\" \\u00E9 foo \\u20AC \\b \\t \\n \\f \\r \\\" \\ \"", Toml.serializeFrom(input));

    // Check unicode code points greater than 0XFFFF
    input = " \uD801\uDC28 \uD840\uDC0B ";
    assertEquals("\" \\U00010428 \\U0002000B \"", Toml.serializeFrom(input));
  }

  @Test
  public void quotesKeys() {
    Map<String, Integer> aMap = new LinkedHashMap<String, Integer>();
    aMap.put("a.b", 1);
    aMap.put("5€", 2);
    aMap.put("c$d", 3);
    aMap.put("e/f", 4);

    String expected = "\"a.b\" = 1\n" +
        "\"5€\" = 2\n" +
        "\"c$d\" = 3\n" +
        "\"e/f\" = 4\n";
    assertEquals(expected, Toml.serializeFrom(aMap));
  }

  @Test
  public void serializesFromToml() {
    String tomlString = "a = 1\n";
    Toml toml = new Toml().parse(tomlString);
    assertEquals(tomlString, toml.serialize());
  }
}