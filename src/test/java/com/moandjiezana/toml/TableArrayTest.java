package com.moandjiezana.toml;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TableArrayTest {

  @Test
  public void should_parse_table_array() throws Exception {
    Toml toml = new Toml().read(file("products_table_array"));

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
  public void should_parse_table_array_out_of_order() throws Exception {
    Toml toml = new Toml().read(file("should_parse_table_array_out_of_order"));
    
    List<Toml> tables = toml.getTables("product");
    List<Toml> employees = toml.getTables("employee");
    
    assertThat(tables, hasSize(2));
    assertThat(tables.get(0).getDouble("price"), equalTo(9.99));
    assertThat(tables.get(1).getString("type"), equalTo("ZX80"));
    assertThat(employees, hasSize(1));
    assertThat(employees.get(0).getString("name"), equalTo("Marinus van der Lubbe"));
  }

  @Test
  public void should_parse_nested_table_arrays() throws Exception {
    Toml toml = new Toml().read(file("fruit_table_array"));

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
    Toml toml = new Toml().read("[[a.b.c]]\n id=3");

    assertEquals(3, toml.getTable("a").getTable("b").getTables("c").get(0).getLong("id").intValue());
  }

  @Test
  public void should_navigate_array_with_compound_key() throws Exception {
    Toml toml = new Toml().read(file("fruit_table_array"));

    List<Toml> appleVarieties = toml.getTables("fruit[0].variety");
    Toml appleVariety = toml.getTable("fruit[0].variety[1]");
    String bananaVariety = toml.getString("fruit[1].variety[0].name");

    assertEquals(2, appleVarieties.size());
    assertEquals("red delicious", appleVarieties.get(0).getString("name"));
    assertEquals("granny smith", appleVariety.getString("name"));
    assertEquals("plantain", bananaVariety);
  }

  @Test
  public void should_return_null_for_missing_table_array() throws Exception {
    Toml toml = new Toml().read("[a]");
    
    assertNull(toml.getTables("b"));
  }

  @Test
  public void should_return_null_for_missing_table_array_with_index() throws Exception {
    Toml toml = new Toml();
    
    assertNull(toml.getTable("a[0]"));
    assertNull(toml.getString("a[0].c"));
  }

  @Test
  public void should_return_null_for_index_out_of_bounds() throws Exception {
    Toml toml = new Toml().read("[[a]]\n  c = 1");
    
    assertNull(toml.getTable("a[1]"));
  }
  
  @Test
  public void should_handle_repeated_tables() {
    new Toml().read("[[a]]\n [a.b]\n [[a]]\n [a.b]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_empty_table_array_name() {
    new Toml().read("[[]]");
  }
  
  private File file(String fileName) {
    return new File(getClass().getResource(fileName + ".toml").getFile());
  }

}
