package com.moandjiezana.toml.tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.moandjiezana.toml.Toml;

public class ErrorMessagesTest {

  @Test
  public void invalid_table() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[in valid]"));
	  assertTrue(e.getMessage().contains("Invalid table definition on line 1: [in valid]"));
  }
  
  @Test
  public void duplicate_table() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[again]\n[again]"));
	  assertTrue(e.getMessage().contains("Duplicate table definition on line 2: [again]"));
  }
  
  @Test
  public void empty_implicit_table_name() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[a..b]"));
	  assertTrue(e.getMessage().contains("Invalid table definition due to empty implicit table name: [a..b]"));
  }
  
  @Test
  public void duplicate_key() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = 1\n  k = 2"));
	  assertTrue(e.getMessage().contains("Duplicate key on line 2: k"));
  }
  
  @Test
  public void invalid_key() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k\" = 1"));
	  assertTrue(e.getMessage().contains("Key is not followed by an equals sign on line 1: k\" = 1"));
  }
  
  @Test
  public void invalid_table_array() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[[in valid]]"));
	  assertTrue(e.getMessage().contains("Invalid table array definition on line 1: [[in valid]]"));
  }
  
  @Test
  public void invalid_value() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = 1 t"));
	  assertTrue(e.getMessage().contains("Invalid text after key k on line 1"));
  }
  
  @Test
  public void unterminated_multiline_literal_string() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = '''abc"));
	  assertTrue(e.getMessage().contains("Unterminated value on line 1: k = '''abc"));
  }
  
  @Test
  public void unterminated_multiline_string() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = \"\"\"abc\"\""));
	  assertTrue(e.getMessage().contains("Unterminated value on line 1: k = \"\"\"abc\"\""));
  }
  
  @Test
  public void unterminated_array() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = [\"abc\""));
	  assertTrue(e.getMessage().contains("Unterminated value on line 1: k = [\"abc\""));
  }
  
  @Test
  public void unterminated_inline_table() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = { a = \"abc\""));
	  assertTrue(e.getMessage().contains("Unterminated value on line 1: k = { a = \"abc\""));
  }
  
  @Test
  public void key_without_equals() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("\nk\n=3"));
	  assertTrue(e.getMessage().contains("Key is not followed by an equals sign on line 2: k"));
  }
  
  @Test
  public void heterogeneous_array() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("k = [ 1,\n  1.1 ]"));
	  assertTrue(e.getMessage().contains("k becomes a heterogeneous array on line 2"));
  }
  
  @Test
  public void key_in_root_is_overwritten_by_table() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("a=1\n  [a]"));
	  assertTrue(e.getMessage().contains("Key already exists for table defined on line 2: [a]"));
  }
  
  @Test
  public void table_is_overwritten_by_key() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[a.b]\n  [a]\n  b=1"));
	  assertTrue(e.getMessage().contains("Table already exists for key defined on line 3: b"));
  }
  
  @Test
  public void should_display_correct_line_number_with_literal_multiline_string() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[table]\n\n k = '''abc\n\ndef\n'''\n # comment \n j = 4.\n l = 5"));
	  assertTrue(e.getMessage().contains("on line 8"));
  }
  
  @Test
  public void should_display_correct_line_number_with_multiline_string() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[table]\n\n k = \"\"\"\nabc\n\ndef\n\"\"\"\n # comment \n j = 4.\n l = 5"));
	  assertTrue(e.getMessage().contains("on line 9"));
  }
  
  @Test
  public void should_display_correct_line_number_with_array() throws Exception {
	  Exception e = assertThrows(Exception.class, () -> new Toml().read("[table]\n\n k = [\"\"\"\nabc\n\ndef\n\"\"\"\n, \n # comment \n j = 4.,\n l = 5\n]"));
	  assertTrue(e.getMessage().contains("on line 10"));
  }
}
