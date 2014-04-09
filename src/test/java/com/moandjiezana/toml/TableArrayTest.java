package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TableArrayTest {

  @Test
  public void should_parse_table_array() throws Exception {
    Toml toml = new Toml().parse(file("products_table_array"));

    List<Toml> products = toml.getTables("products");

    assertEquals(3, products.size());

    assertEquals("Hammer", products.get(0).getString("name"));
    assertEquals(738594937L, products.get(0).getLong("sku").longValue());

    Assert.assertNull(products.get(1).getString("name"));
    assertNull(products.get(1).getLong("sku"));

    assertEquals("Nail", products.get(2).getString("name"));
    assertEquals(284758393L, products.get(2).getLong("sku").longValue());
    assertEquals("gray", products.get(2).getString("color"));
  }

  @Test
  public void should_parse_nested_table_arrays() throws Exception {
    Toml toml = new Toml().parse(file("fruit_table_array"));

    List<Toml> fruits = toml.getTables("fruit");

    assertEquals(2, fruits.size());

    Toml apple = fruits.get(0);
    assertEquals("apple", apple.getString("name"));
    assertEquals("red", apple.getTable("physical").getString("color"));
    assertEquals("round", apple.getTable("physical").getString("shape"));
    assertEquals(2, apple.getTables("variety").size());

    Toml banana = fruits.get(1);
    assertEquals("banana", banana.getString("name"));
    assertEquals(1, banana.getTables("variety").size());
    assertEquals("plantain", banana.getTables("variety").get(0).getString("name"));
  }

  @Test
  public void should_create_array_ancestors_as_tables() throws Exception {
    Toml toml = new Toml().parse("[[a.b.c]]\n id=3");

    assertEquals(3, toml.getTable("a").getTable("b").getTables("c").get(0).getLong("id").intValue());
  }

  @Test
  public void should_navigate_array_with_compound_key() throws Exception {
    Toml toml = new Toml().parse(file("fruit_table_array"));

    List<Toml> appleVarieties = toml.getTables("fruit[0].variety");
    Toml appleVariety = toml.getTable("fruit[0].variety[1]");
    String bananaVariety = toml.getString("fruit[1].variety[0].name");

    assertEquals(2, appleVarieties.size());
    assertEquals("red delicious", appleVarieties.get(0).getString("name"));
    assertEquals("granny smith", appleVariety.getString("name"));
    assertEquals("plantain", bananaVariety);
  }

  private File file(String fileName) {
    return new File(getClass().getResource(fileName + ".toml").getFile());
  }

}
