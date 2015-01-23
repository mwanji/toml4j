package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BareKeysTest {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used_in_table_name() throws Exception {
    exception.expect(IllegalStateException.class);
    exception.expectMessage("Invalid table definition: [~]");
    
    new Toml().parse("[~]");
  }

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used_in_key_name() throws Exception {
    exception.expect(IllegalStateException.class);
    
    new Toml().parse("~ = 1");
  }

  @Test
  public void should_fail_on_sharp_sign_in_table_name() throws Exception {
    exception.expect(IllegalStateException.class);

    new Toml().parse("[group#]\nkey=1");
  }
  
  @Test
  public void should_fail_on_spaces_in_table_name() throws Exception {
    exception.expect(IllegalStateException.class);

    new Toml().parse("[valid  key]");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_question_marks_in_key_name() throws Exception {
    new Toml().parse("key?=true");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_empty_table_name() {
    new Toml().parse("[]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_nested_table_name_ending_with_empty_table_name() {
    new Toml().parse("[a.]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_nested_table_name_containing_empty_table_name() {
    new Toml().parse("[a..b]");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_nested_table_name_starting_with_empty_table_name() {
    new Toml().parse("[.b]");
  }
}
