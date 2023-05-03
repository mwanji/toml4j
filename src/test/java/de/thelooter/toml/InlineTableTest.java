package de.thelooter.toml;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.thelooter.toml.Toml;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InlineTableTest {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
  
  @Rule
  public ExpectedException e = ExpectedException.none();

  @Test
  public void should_read_empty_inline_table() throws Exception {
    Toml toml = new Toml().read("key = {}");
    
    assertTrue(toml.getTable("key").isEmpty());
  }
  
  @Test
  public void should_read_inline_table_with_strings() throws Exception {
    Toml toml = new Toml().read("name = { first = \"Tom\", last = \"Preston-Werner\"}");
    
    assertEquals("Tom", toml.getTable("name").getString("first"));
    assertEquals("Preston-Werner", toml.getString("name.last"));
  }
  
  @Test
  public void should_read_inline_table_with_integers() throws Exception {
    Toml toml = new Toml().read("point = { x = 1, y = 2 }");
    
    assertEquals(1, toml.getTable("point").getLong("x").longValue());
    assertEquals(2, toml.getLong("point.y").longValue());
  }
  
  @Test
  public void should_read_inline_table_with_floats() throws Exception {
    Toml toml = new Toml().read("point = { x = 1.5, y = 2.3 }");
    
    assertEquals(1.5, toml.getTable("point").getDouble("x").doubleValue(), 0);
    assertEquals(2.3, toml.getDouble("point.y").doubleValue(), 0);
  }
  
  @Test
  public void should_read_inline_table_with_booleans() throws Exception {
    Toml toml = new Toml().read("point = { x = false, y = true }");
    
    assertTrue(toml.getTable("point").getBoolean("y"));
    assertFalse(toml.getBoolean("point.x"));
  }
  
  @Test
  public void should_read_inline_table_with_dates() throws Exception {
    Toml toml = new Toml().read("point = { x = 2015-02-09T22:05:00Z, y = 2015-02-09T21:05:00Z }");

    Calendar x = Calendar.getInstance(UTC);
    x.set(2015, Calendar.FEBRUARY, 9, 22, 5, 00);
    x.set(Calendar.MILLISECOND, 0);
    
    Calendar y = Calendar.getInstance(UTC);
    y.set(2015, Calendar.FEBRUARY, 9, 21, 5, 00);
    y.set(Calendar.MILLISECOND, 0);

    assertEquals(x.getTime(), toml.getTable("point").getDate("x"));
    assertEquals(y.getTime(), toml.getDate("point.y"));
  }
  
  @Test
  public void should_read_arrays() throws Exception {
    Toml toml = new Toml().read("arrays = { integers = [1, 2, 3], strings = [\"a\", \"b\", \"c\"] }");
    
    assertThat(toml.<Long>getList("arrays.integers"), contains(1L, 2L, 3L));
    assertThat(toml.<String>getList("arrays.strings"), contains("a", "b", "c"));
  }
  
  @Test
  public void should_read_nested_arrays() throws Exception {
    Toml toml = new Toml().read("arrays = { nested = [[1, 2, 3], [4, 5, 6]] }").getTable("arrays");
    
    List<List<Long>> nested = toml.<List<Long>>getList("nested");
    assertThat(nested, hasSize(2));
    assertThat(nested.get(0), contains(1L, 2L, 3L));
    assertThat(nested.get(1), contains(4L, 5L, 6L));
  }
  
  @Test
  public void should_read_mixed_inline_table() throws Exception {
    Toml toml = new Toml().read("point = { date = 2015-02-09T22:05:00Z, bool = true, integer = 123, float = 123.456, string = \"abc\", list = [5, 6, 7, 8] }").getTable("point");


    Calendar date = Calendar.getInstance(UTC);
    date.set(2015, Calendar.FEBRUARY, 9, 22, 5, 00);
    date.set(Calendar.MILLISECOND, 0);
    
    assertEquals(date.getTime(), toml.getDate("date"));
    assertTrue(toml.getBoolean("bool"));
    assertEquals(123, toml.getLong("integer").intValue());
    assertEquals(123.456, toml.getDouble("float"), 0);
    assertEquals("abc", toml.getString("string"));
    assertThat(toml.<Long>getList("list"), contains(5L, 6L, 7L, 8L));
  }
  
  @Test
  public void should_read_nested_inline_tables() throws Exception {
    Toml tables = new Toml().read("tables = { t1 = { t1_1 = 1, t1_2 = 2}, t2 = { t2_1 = { t2_1_1 = \"a\" }} }").getTable("tables");
    
    assertEquals(1L, tables.getLong("t1.t1_1").longValue());
    assertEquals(2L, tables.getLong("t1.t1_2").longValue());
    assertEquals("a", tables.getString("t2.t2_1.t2_1_1"));
  }
  
  @Test
  public void should_read_all_string_types() throws Exception {
    Toml strings = new Toml().read("strings = { literal = 'ab]\"c', multiline = \"\"\"de]\"f\"\"\", multiline_literal = '''gh]\"i''' }").getTable("strings");
    
    assertEquals("ab]\"c", strings.getString("literal"));
    assertEquals("de]\"f", strings.getString("multiline"));
    assertEquals("gh]\"i", strings.getString("multiline_literal"));
  }
  
  @Test
  public void should_read_inline_table_in_regular_table() throws Exception {
    Toml toml = new Toml().read("[tbl]\n tbl = { tbl = 1 }");
    
    assertEquals(1, toml.getLong("tbl.tbl.tbl").intValue());
  }
  
  @Test
  public void should_mix_with_tables() throws Exception {
    Toml toml = new Toml().read("t = { k = 1 }\n  [b]\n  k = 2\n  t = { k = 3}");
    
    assertEquals(1, toml.getLong("t.k").intValue());
    assertEquals(2, toml.getLong("b.k").intValue());
    assertEquals(3, toml.getLong("b.t.k").intValue());
  }
  
  @Test
  public void should_add_properties_to_existing_inline_table() throws Exception {
    Toml toml = new Toml().read("[a]\n  b = {k = 1}\n  [a.b.c]\n k = 2");
    
    assertEquals(1, toml.getLong("a.b.k").intValue());
    assertEquals(2, toml.getLong("a.b.c.k").intValue());
  }
  
  @Test
  public void should_mix_with_table_arrays() throws Exception {
    Toml toml = new Toml().read("t = { k = 1 }\n  [[b]]\n  t = { k = 2 }\n [[b]]\n  t = { k = 3 }");
    
    assertEquals(1, toml.getLong("t.k").intValue());
    assertEquals(2, toml.getLong("b[0].t.k").intValue());
    assertEquals(3, toml.getLong("b[1].t.k").intValue());
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_key() throws Exception {
    new Toml().read("tbl = { a. = 1 }");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_when_unterminated() throws Exception {
    new Toml().read("tbl = { a = 1 ");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_value() throws Exception {
    new Toml().read("tbl = { a = abc }");
  }
  
  @Test
  public void should_fail_when_key_duplicated_inside_inline_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate key on line 1: a");
    
    new Toml().read("tbl = { a = 1, a = 2 }");
  }
  
  @Test
  public void should_fail_when_duplicated_by_other_key() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Table already exists for key defined on line 2: tbl");
    
    new Toml().read("tbl = { a = 1 }\n tbl = 1");
  }
  
  @Test
  public void should_fail_when_duplicated_by_other_inline_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate table definition on line 2: [tbl]");
    
    new Toml().read("tbl = { a = 1 }\n tbl = {}");
  }
  
  @Test
  public void should_fail_when_duplicated_by_top_level_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate table definition on line 2: [tbl]");
    
    new Toml().read("tbl = {}\n [tbl]");
  }
  
  @Test
  public void should_fail_when_duplicates_second_level_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate table definition on line 3: [a.b]");
    
    new Toml().read("[a.b]\n  [a]\n b = {}");
  }
  
  @Test
  public void should_fail_when_inline_table_duplicates_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate table definition on line 3: [a.b]");
    
    new Toml().read("[a.b]\n [a]\n b = {}");
  }
  
  @Test
  public void should_fail_when_second_level_table_duplicates_inline_table() throws Exception {
    e.expect(IllegalStateException.class);
    e.expectMessage("Duplicate table definition on line 3: [a.b]");
    
    new Toml().read("[a]\n b = {}\n  [a.b]");
  }
}
