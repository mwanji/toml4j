package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ErrorMessagesTest {

  @Rule
  public final ExpectedException e = ExpectedException.none();
  
  @Test
  public void invalid_table() throws Exception {
    e.expectMessage("Invalid table definition on line 1: [in valid]");
    
    new Toml().read("[in valid]");
  }
  
  @Test
  public void duplicate_table() throws Exception {
    e.expectMessage("Duplicate table definition on line 2: [again]");
    
    new Toml().read("[again]\n[again]");
  }
  
  @Test
  public void empty_implicit_table_name() throws Exception {
    e.expectMessage("Invalid table definition due to empty implicit table name: [a..b]");
    
    new Toml().read("[a..b]");
  }
  
  @Test
  public void duplicate_key() throws Exception {
    e.expectMessage("Duplicate key on line 2: k");
    
    new Toml().read("k = 1\n  k = 2");
  }
  
  @Test
  public void invalid_key() throws Exception {
    e.expectMessage("Key is not followed by an equals sign on line 1: k\" = 1");
    
    new Toml().read("k\" = 1");
  }
  
  @Test
  public void invalid_table_array() throws Exception {
    e.expectMessage("Invalid table array definition on line 1: [[in valid]]");
    
    new Toml().read("[[in valid]]");
  }
  
  @Test
  public void invalid_value() throws Exception {
    e.expectMessage("Invalid text after key k on line 1");
    
    new Toml().read("k = 1 t");
  }
  
  @Test
  public void unterminated_multiline_literal_string() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = '''abc");
    
    new Toml().read("k = '''abc");
  }
  
  @Test
  public void unterminated_multiline_string() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = \"\"\"abc\"\"");
    
    new Toml().read("k = \"\"\"abc\"\"");
  }
  
  @Test
  public void unterminated_array() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = [\"abc\"");
    
    new Toml().read("k = [\"abc\"");
  }
  
  @Test
  public void unterminated_inline_table() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = { a = \"abc\"");
    
    new Toml().read("k = { a = \"abc\"");
  }
  
  @Test
  public void key_without_equals() throws Exception {
    e.expectMessage("Key is not followed by an equals sign on line 2: k");
    
    new Toml().read("\nk\n=3");
  }
  
  @Test
  public void heterogeneous_array() throws Exception {
    e.expectMessage("k becomes a heterogeneous array on line 2");
    
    new Toml().read("k = [ 1,\n  1.1 ]");
  }
  
  @Test
  public void key_in_root_is_overwritten_by_table() throws Exception {
    e.expectMessage("Key already exists for table defined on line 2: [a]");
    
    new Toml().read("a=1\n  [a]");
  }
  
  @Test
  public void table_is_overwritten_by_key() throws Exception {
    e.expectMessage("Table already exists for key defined on line 3: b");
    
    new Toml().read("[a.b]\n  [a]\n  b=1");
  }
  
  @Test
  public void should_display_correct_line_number_with_literal_multiline_string() throws Exception {
    e.expectMessage("on line 8");
    
    new Toml().read("[table]\n\n k = '''abc\n\ndef\n'''\n # comment \n j = 4.\n l = 5");
  }
  
  @Test
  public void should_display_correct_line_number_with_multiline_string() throws Exception {
    e.expectMessage("on line 9");
    
    new Toml().read("[table]\n\n k = \"\"\"\nabc\n\ndef\n\"\"\"\n # comment \n j = 4.\n l = 5");
  }
  
  @Test
  public void should_display_correct_line_number_with_array() throws Exception {
    e.expectMessage("on line 10");
    
    new Toml().read("[table]\n\n k = [\"\"\"\nabc\n\ndef\n\"\"\"\n, \n # comment \n j = 4.,\n l = 5\n]");
  }
}
