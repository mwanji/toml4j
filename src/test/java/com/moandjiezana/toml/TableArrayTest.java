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
    Toml toml = new Toml().parse(new File(getClass().getResource("products_table_array.toml").getFile()));

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
}
