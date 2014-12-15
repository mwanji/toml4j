package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.moandjiezana.toml.testutils.Utils;

public class StringTest {

  @Test
  public void should_get_string() throws Exception {
    Toml toml = new Toml().parse("a = \"a\"");

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string() {
    Toml toml = new Toml().parse("a = \"\"");
    assertEquals("", toml.getString("a"));
  }

  @Test
  public void should_get_empty_string_with_trailing_new_line() {
    Toml toml = new Toml().parse("a = \"\"\n");
    assertEquals("", toml.getString("a"));
  }
  
  @Test
  public void should_get_basic_multiline_string() throws Exception {
    Toml toml = new Toml().parse(file("should_get_basic_multiline_string"));
    
    assertEquals(toml.getString("ref"), toml.getString("one_line"));
    assertEquals(toml.getString("ref"), toml.getString("many_lines"));
  }
  
  @Test
  public void should_get_multiline_string_without_new_lines() throws Exception {
    Toml toml = new Toml().parse(file("should_get_multiline_string_without_new_lines"));
    
    assertEquals(toml.getString("ref"), toml.getString("multi1"));
    assertEquals(toml.getString("ref"), toml.getString("multi2"));
  }
  
  @Test
  public void should_get_literal_string() throws Exception {
    Toml toml = new Toml().parse(file("should_get_literal_string"));
    
    assertEquals("C:\\Users\\nodejs\\templates", toml.getString("winpath"));
    assertEquals("\\\\ServerX\\admin$\\system32\\", toml.getString("winpath2"));
    assertEquals("Tom \"Dubs\" Preston-Werner", toml.getString("quoted"));
    assertEquals("<\\i\\c*\\s*>", toml.getString("regex"));
  }
  
  @Test
  public void should_get_multiline_literal_string() throws Exception {
    Toml toml = new Toml().parse(file("should_get_multiline_literal_string"));
    
    assertTrue(toml.getString("empty_line").isEmpty());
    assertEquals(toml.getString("regex2_ref"), toml.getString("regex2"));
    assertEquals(toml.getString("lines_ref"), toml.getString("lines"));
  }

  @Test
  public void should_support_special_characters_in_strings() {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_support_special_characters_in_strings.toml").getFile()));

    assertEquals("\" \t \n \r \\ / \b \f", toml.getString("key"));
  }

  @Test
  public void should_support_unicode_characters_in_strings() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_support_special_characters_in_strings.toml").getFile()));

    assertEquals("more or less ±", toml.getString("unicode_key"));
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_reserved_special_character_in_strings() throws Exception {
    new Toml().parse("key=\"\\m\"");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_text_after_literal_string() {
    new Toml().parse("a = ' ' jdkf");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_unterminated_literal_string() throws Exception {
    new Toml().parse("a = 'some text");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_unterminated_multiline_literal_string() throws Exception {
    new Toml().parse("a = '''some\n text\n''\nb = '''1'''");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_unterminated_multiline_literal_string_on_single_line() throws Exception {
    new Toml().parse("a = '''some text''");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_text_outside_multiline_string() {
    new Toml().parse("a = \"\"\" \"\"\" jdkf");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_unterminated_multiline_string() throws Exception {
    new Toml().parse("a = \"\"\"some text\"\"");
  }

  private File file(String file) {
    return Utils.file(getClass(), file);
  }
}
