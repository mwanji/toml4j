package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.moandjiezana.toml.testutils.Utils;

public class IteratorTest {

  @Test
  public void should_iterate_over_primitive() throws Exception {
    Toml toml = new Toml().parse(file("long"));
    Toml.Entry entry = toml.entrySet().iterator().next();
    
    assertEquals("long", entry.getKey());
    assertEquals(2L, entry.getValue());
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_list() throws Exception {
    Toml toml = new Toml().parse(file("list"));
    
    Toml.Entry entry = toml.entrySet().iterator().next();
    
    assertEquals("list", entry.getKey());
    assertThat((List<String>) entry.getValue(), contains("a", "b", "c"));
  }
  
  @Test
  public void should_iterate_over_table() throws Exception {
    Toml toml = new Toml().parse(file("table"));
    Toml.Entry entry = toml.entrySet().iterator().next();
    
    assertEquals("table", entry.getKey());
    assertEquals("a", ((Toml) entry.getValue()).getString("a"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_table_array() throws Exception {
    Toml toml = new Toml().parse(file("table_array"));
    
    Toml.Entry entry = toml.entrySet().iterator().next();
    List<Toml> tableArray = (List<Toml>) entry.getValue();
    
    assertEquals("table_array", entry.getKey());
    assertThat(tableArray, contains(instanceOf(Toml.class), instanceOf(Toml.class)));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_multiple_entries() throws Exception {
    Toml toml = new Toml().parse(file("multiple"));

    Map<String, Object> entries = new HashMap<String, Object>();
    for (Toml.Entry entry : toml.entrySet()) {
      entries.put(entry.getKey(), entry.getValue());
    }
    
    assertThat(entries.keySet(), containsInAnyOrder("a", "b", "c", "e"));
    assertThat(entries, hasEntry("a", (Object) "a"));
    assertThat(entries, hasEntry("b", (Object) asList(1L, 2L, 3L)));
    assertTrue(((Toml) entries.get("c")).getBoolean("d"));
    assertThat(((List<Toml>) entries.get("e")), hasSize(1));
  }
  
  private File file(String name) {
    return Utils.file(getClass(), "/IteratorTest/" + name);
  }
}
