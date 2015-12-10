package com.moandjiezana.toml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ContainsTest {

  @Test
  public void should_contain_top_level_of_any_type() throws Exception {
    Toml toml = new Toml().read("a = 1  \n  [b]  \n  b1 = 1  \n  [[c]]  \n c1 = 1");
    
    assertTrue(toml.contains("a"));
    assertTrue(toml.contains("b"));
    assertTrue(toml.contains("c"));
    assertFalse(toml.contains("d"));
  }
  
  @Test
  public void should_contain_nested_of_any_type() throws Exception {
    Toml toml = new Toml().read("[a]  \n  a1 = 1  \n  [[b]]  \n b1 = 1  \n  [[b]]  \n b1 = 2  \n  b2 = 3");
    
    assertTrue(toml.contains("a.a1"));
    assertTrue(toml.contains("b[0].b1"));
    assertTrue(toml.contains("b[1].b1"));
    assertFalse(toml.contains("b[2].b1"));
    assertFalse(toml.contains("c.d"));
  }
  
  @Test
  public void should_contain_primitive() throws Exception {
    Toml toml = new Toml().read("a = 1  \n  [b]  \n  b1 = 1  \n  [[c]]  \n c1 = 1");
    
    assertTrue(toml.containsPrimitive("a"));
    assertTrue(toml.containsPrimitive("b.b1"));
    assertTrue(toml.containsPrimitive("c[0].c1"));
    assertFalse(toml.containsPrimitive("b"));
    assertFalse(toml.containsPrimitive("c"));
    assertFalse(toml.containsPrimitive("d"));
  }
  
  @Test
  public void should_contain_table() throws Exception {
    Toml toml = new Toml().read("a = 1  \n  [b]  \n  b1 = 1  \n  [b.b2]  \n  [[c]]  \n c1 = 1  \n [c.c2]");

    assertTrue(toml.containsTable("b"));
    assertTrue(toml.containsTable("b.b2"));
    assertTrue(toml.containsTable("c[0].c2"));
    assertFalse(toml.containsTable("a"));
    assertFalse(toml.containsTable("b.b1"));
    assertFalse(toml.containsTable("c"));
    assertFalse(toml.containsTable("c[0].c1"));
    assertFalse(toml.containsTable("d"));
  }
  
  @Test
  public void should_contain_table_array() throws Exception {
    Toml toml = new Toml().read("a = 1  \n  [b]  \n  b1 = 1  \n  [[c]]  \n c1 = 1  \n [c.c2] \n  [[c]]  \n  [[c.c3]] \n c4 = 4");
    
    assertTrue(toml.containsTableArray("c"));
    assertTrue(toml.containsTableArray("c[1].c3"));
    assertFalse(toml.containsTableArray("a"));
    assertFalse(toml.containsTableArray("b"));
    assertFalse(toml.containsTableArray("b.b1"));
    assertFalse(toml.containsTableArray("c[1].c3[0].c4"));
    assertFalse(toml.containsTableArray("d"));
  }
  
  @Test
  public void should_not_contain_when_parent_table_is_missing() throws Exception {
    Toml toml = new Toml().read("a = \"1\"");
    
    assertFalse(toml.contains("b.b1"));
    assertFalse(toml.containsPrimitive("b.b1"));
    assertFalse(toml.containsTable("b.b1"));
    assertFalse(toml.containsTableArray("b.b1"));
  }
}
