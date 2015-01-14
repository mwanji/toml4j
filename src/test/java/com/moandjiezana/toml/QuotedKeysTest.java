package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

public class QuotedKeysTest {

  @Test
  public void should_accept_quoted_key_for_value() throws Exception {
    Toml toml = new Toml().parse("\"127.0.0.1\" = \"localhost\"  \n  \"character encoding\" = \"UTF-8\" \n  \"ʎǝʞ\" = \"value\"");
    
    assertEquals("localhost", toml.getString("\"127.0.0.1\""));
    assertEquals("UTF-8", toml.getString("\"character encoding\""));
    assertEquals("value", toml.getString("\"ʎǝʞ\""));
  }
  
  @Test
  public void should_accept_quoted_key_for_table_name() throws Exception {
    Toml toml = new Toml().parse("[\"abc def\"]\n  val = 1");
    
    assertEquals(1L, toml.getTable("\"abc def\"").getLong("val").longValue());
  }
  
  @Test
  public void should_accept_partially_quoted_table_name() throws Exception {
    Toml toml = new Toml().parse("[dog.\"tater.man\"]  \n  type = \"pug0\"  \n[dog.tater]  \n  type = \"pug1\"\n[dog.tater.man]  \n  type = \"pug2\"");
    Toml dogs = toml.getTable("dog");
    
    assertEquals("pug0", dogs.getTable("\"tater.man\"").getString("type"));
    assertEquals("pug1", dogs.getTable("tater").getString("type"));
    assertEquals("pug2", dogs.getTable("tater").getTable("man").getString("type"));
    assertEquals("pug0", toml.getString("dog.\"tater.man\".type"));
    assertEquals("pug2", toml.getString("dog.tater.man.type"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_conserve_quoted_key_in_map() throws Exception {
    Toml toml = new Toml().parse("[dog.\"tater.man\"]  \n  type = \"pug0\"  \n[dog.tater]  \n  type = \"pug1\"\n[dog.tater.man]  \n  type = \"pug2\"");
    Toml dogs = toml.getTable("dog");
    
    Map<String, Map<String, Object>> map = dogs.to(Map.class);
    
    assertEquals("pug0", map.get("\"tater.man\"").get("type"));
    assertEquals("pug1", map.get("tater").get("type"));
    assertEquals("pug2", ((Map<String, Object>) map.get("tater").get("man")).get("type"));
  }
  
  @Test
  public void should_convert() throws Exception {
    Quoted quoted = new Toml().parse("\"ʎǝʞ\" = \"value\"  \n[map]  \n  \"ʎǝʞ\" = \"value\"").to(Quoted.class);
    
    assertNull(quoted.ʎǝʞ);
    assertEquals("value", quoted.map.get("\"ʎǝʞ\""));
  }
  
  private static class Quoted {
    
    String ʎǝʞ;
    
    Map<String, Object> map;
  }
}
