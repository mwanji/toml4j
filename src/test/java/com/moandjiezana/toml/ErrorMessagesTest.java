package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ErrorMessagesTest {

  @Rule
  public final ExpectedException e = ExpectedException.none();
  
  @Test
  public void should_message_invalid_table() throws Exception {
    e.expectMessage("Invalid table definition on line 1: [in valid]");
    
    new Toml().parse("[in valid]");
  }
  
  @Test
  public void should_message_duplicate_table() throws Exception {
    e.expectMessage("Duplicate table definition: [again]");
    
    new Toml().parse("[again]\n[again]");
  }
  
  @Test
  public void should_message_empty_implicit_table_name() throws Exception {
    e.expectMessage("Invalid table definition due to empty implicit table name: [a..b]");
    
    new Toml().parse("[a..b]");
  }
  
  @Test
  public void should_message_duplicate_key() throws Exception {
    e.expectMessage("Duplicate key: k");
    
    new Toml().parse("k = 1\n  k = 2");
  }
  
  @Test
  public void should_message_invalid_key() throws Exception {
    e.expectMessage("Invalid key on line 1: k\"");
    
    new Toml().parse("k\" = 1");
  }
  
  @Test
  public void should_message_invalid_table_array() throws Exception {
    e.expectMessage("Invalid table array definition on line 1: [[in valid]]");
    
    new Toml().parse("[[in valid]]");
  }
  
  @Test
  public void should_message_invalid_value() throws Exception {
    e.expectMessage("Invalid text after key k on line 1");
    
    new Toml().parse("k = 1 t");
  }
  
  @Test
  public void should_message_unterminated_multiline_literal_string() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = '''abc");
    
    new Toml().parse("k = '''abc");
  }
  
  @Test
  public void should_message_unterminated_multiline_string() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = \"\"\"abc\"\"");
    
    new Toml().parse("k = \"\"\"abc\"\"");
  }
  
  @Test
  public void should_message_unterminated_array() throws Exception {
    e.expectMessage("Unterminated value on line 1: k = [\"abc\"");
    
    new Toml().parse("k = [\"abc\"");
  }
  
  @Test
  public void should_message_key_without_equals() throws Exception {
    e.expectMessage("Key k is not followed by an equals sign on line 2");
    
    new Toml().parse("\nk\n=3");
  }
  
  @Test
  public void should_display_correct_line_number_with_literal_multiline_string() throws Exception {
    e.expectMessage("on line 7");
    
    new Toml().parse("[table]\n\n k = '''abc\n\ndef\n'''\n # comment \n j = 4.\n l = 5");
  }
  
  @Test
  public void should_display_correct_line_number_with_multiline_string() throws Exception {
    e.expectMessage("on line 8");
    
    new Toml().parse("[table]\n\n k = \"\"\"\nabc\n\ndef\n\"\"\"\n # comment \n j = 4.\n l = 5");
  }
  
  @Test
  public void should_display_correct_line_number_with_array() throws Exception {
    e.expectMessage("on line 9");
    
    new Toml().parse("[table]\n\n k = [\"\"\"\nabc\n\ndef\n\"\"\"\n, \n # comment \n j = 4.,\n l = 5\n]");
  }
  
  @Test
  public void should_message_heterogeneous_array() throws Exception {
    e.expectMessage("k becomes a heterogeneous array on line 2");
    
    new Toml().parse("k = [ 1,\n  1.1 ]");
  }
}
