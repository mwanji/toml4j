package de.thelooter.toml;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class TomlTest {
  
  @Test
  public void should_return_null_if_no_value_for_key() {
    Toml toml = new Toml().read("");

    assertNull(toml.getString("a"));
  }

  @Test
  public void should_load_from_file() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_load_from_file.toml"));

    assertEquals("value", toml.getString("key"));
  }

  @Test
  public void should_support_blank_lines() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_support_blank_line.toml"));

    assertEquals(1, toml.getLong("group.key").intValue());
  }

  @Test
  public void should_allow_comment_after_values() {
    Toml toml = new Toml().read(new File("src/test/resources/de/thelooter/toml/should_allow_comment_after_values.toml"));

    assertEquals(1, toml.getLong("a").intValue());
    assertEquals(1.1, toml.getDouble("b").doubleValue(), 0);
    assertEquals("abc", toml.getString("c"));
    Calendar cal = Calendar.getInstance();
    cal.set(2014, Calendar.AUGUST, 4, 13, 47, 0);
    cal.set(Calendar.MILLISECOND, 0);
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(cal.getTime(), toml.getDate("d"));
    assertThat(toml.<String>getList("e"), Matchers.contains("a", "b"));
    assertTrue(toml.getBoolean("f"));
    assertEquals("abc", toml.getString("g"));
    assertEquals("abc", toml.getString("h"));
    assertEquals("abc\nabc", toml.getString("i"));
    assertEquals("abc\nabc", toml.getString("j"));
  }
  
  @Test
  public void should_be_empty_if_no_values() {
    assertTrue(new Toml().isEmpty());
    assertFalse(new Toml().read("a = 1").isEmpty());
  }

  @Test
  public void should_fail_on_empty_key_name()  {
    assertThrows(IllegalStateException.class, () -> new Toml().read(" = 1"));
  }
  
  @Test
  public void should_fail_on_key_name_with_hash() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a# = 1"));
  }
  
  @Test
  public void should_fail_on_key_name_starting_with_square_bracket() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("[a = 1"));

  }

  @Test
  public void should_fail_when_key_is_overwritten_by_table() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("[a]\nb=1\n[a.b]\nc=2"));
  }
  
  @Test
  public void should_fail_when_key_in_root_is_overwritten_by_table() {
    assertThrows(IllegalStateException.class, () -> new Toml().read("a=1\n  [a]"));
  }

  @Test
  public void should_fail_when_key_is_overwritten_by_another_key() {
    assertThrows(IllegalStateException.class, () -> {
      new Toml().read("[fruit]\ntype=\"apple\"\ntype=\"orange\"");
    });
  }
}
