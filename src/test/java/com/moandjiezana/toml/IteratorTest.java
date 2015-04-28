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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.moandjiezana.toml.testutils.Utils;

public class IteratorTest {
  
  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Test
  public void should_iterate_over_primitive() throws Exception {
    Toml toml = new Toml().parse(file("long"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("long", entry.getKey());
    assertEquals(2L, entry.getValue());
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_list() throws Exception {
    Toml toml = new Toml().parse(file("list"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("list", entry.getKey());
    assertThat((List<String>) entry.getValue(), contains("a", "b", "c"));
  }
  
  @Test
  public void should_iterate_over_table() throws Exception {
    Toml toml = new Toml().parse(file("table"));
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    
    assertEquals("table", entry.getKey());
    assertEquals("a", ((Toml) entry.getValue()).getString("a"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_table_array() throws Exception {
    Toml toml = new Toml().parse(file("table_array"));
    
    Map.Entry<String, Object> entry = toml.entrySet().iterator().next();
    List<Toml> tableArray = (List<Toml>) entry.getValue();
    
    assertEquals("table_array", entry.getKey());
    assertThat(tableArray, contains(instanceOf(Toml.class), instanceOf(Toml.class)));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void should_iterate_over_multiple_entries() throws Exception {
    Toml toml = new Toml().parse(file("multiple"));

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
  public void should_not_support_setValue_method() throws Exception {
    Map.Entry<String, Object> entry = new Toml().parse("a = 1").entrySet().iterator().next();
    
    expectedException.expect(UnsupportedOperationException.class);
    entry.setValue(2L);
  }
  
  private File file(String name) {
    return Utils.file(getClass(), "/IteratorTest/" + name);
  }
}
