package de.thelooter.toml;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class IterationTest {
  

  @Test
  public void should_iterate_over_primitive() {
    Toml toml = new Toml().read(new File("src/test/resources/IteratorTest/long.toml"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("long", entry.getKey());
    assertEquals(2L, entry.getValue());
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_list() {
    Toml toml = new Toml().read(new File("src/test/resources/IteratorTest/list.toml"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("list", entry.getKey());
    assertThat((List<String>) entry.getValue(), contains("a", "b", "c"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_empty_list() {
    Toml toml = new Toml().read("list = []");
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("list", entry.getKey());
    assertThat((List<String>) entry.getValue(), empty());
  }
  
  @Test
  public void should_iterate_over_table() {
    Toml toml = new Toml().read(new File("src/test/resources/IteratorTest/table.toml"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("table", entry.getKey());
    assertEquals("a", ((Toml) entry.getValue()).getString("a"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_table_array() {
    Toml toml = new Toml().read(new File("src/test/resources/IteratorTest/table_array.toml"));
    
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    List<Toml> tableArray = (List<Toml>) entry.getValue();
    
    assertEquals("table_array", entry.getKey());
    assertThat(tableArray, contains(instanceOf(Toml.class), instanceOf(Toml.class)));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_multiple_entries() {
    Toml toml = new Toml().read(new File("src/test/resources/IteratorTest/multiple.toml"));

    Map<String, Object> entries = new HashMap<String, Object>();
    for (Map.Entry<String, Object> entry : toml.entrySet()) {
      entries.put(entry.getKey(), entry.getValue());
    }
    
    assertThat(entries.keySet(), containsInAnyOrder("a", "b", "c", "e"));
    assertThat(entries, hasEntry("a", (Object) "a"));
    assertThat(entries, hasEntry("b", (Object) asList(1L, 2L, 3L)));
    assertTrue(((Toml) entries.get("c")).getBoolean("d"));
    assertThat(((List<Toml>) entries.get("e")), hasSize(1));
  }
  
  @Test
  public void should_not_support_setValue_method() {
    Map.Entry<String, Object> entry = new Toml().read("a = 1").entrySet().iterator().next();
    
    assertThrows(UnsupportedOperationException.class, () -> entry.setValue(2L));
  }
  
  @Test
  public void should_not_iterate_over_empty_toml() {
    Toml toml = new Toml().read("");
    
    assertThat(toml.entrySet(), empty());
  }
  
}
