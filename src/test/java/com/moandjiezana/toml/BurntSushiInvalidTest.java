package com.moandjiezana.toml;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class BurntSushiInvalidTest {
  private InputStream inputToml;
  
  @Test
  public void key_empty() throws Exception {
    runInvalidTest("key-empty");
  }
  
  @Test
  public void key_hash() throws Exception {
    runInvalidTest("key-hash");
  }
  
  @Test
  public void key_newline() throws Exception {
    runInvalidTest("key-newline");
  }
  
  @Test
  public void key_open_bracket() throws Exception {
    runInvalidTest("key-open-bracket");
  }
  
  @Test
  public void key_single_open_bracket() throws Exception {
    runInvalidTest("key-single-open-bracket");
  }
  
  @Test
  public void key_start_bracket() throws Exception {
    runInvalidTest("key-start-bracket");
  }
  
  @Test
  public void key_two_equals() throws Exception {
    runInvalidTest("key-two-equals");
  }
  
  @Test
  public void string_bad_byte_escape() throws Exception {
    runInvalidTest("string-bad-byte-escape");
  }
  
  @Test
  public void string_bad_escape() throws Exception {
    runInvalidTest("string-bad-escape");
  }
  
  @Test
  public void string_byte_escapes() throws Exception {
    runInvalidTest("string-byte-escapes");
  }
  
  @Test
  public void string_no_close() throws Exception {
    runInvalidTest("string-no-close");
  }

  @Test
  public void table_array_implicit() throws Exception {
    runInvalidTest("table-array-implicit");
  }

  @Test
  public void table_array_malformed_bracket() throws Exception {
    runInvalidTest("table-array-malformed-bracket");
  }
  
  @Test
  public void table_array_malformed_empty() throws Exception {
    runInvalidTest("table-array-malformed-empty");
  }
  
  @Test
  public void table_empty() throws Exception {
    runInvalidTest("table-empty");
  }
  
  @Test
  public void table_nested_brackets_close() throws Exception {
    runInvalidTest("table-nested-brackets-close");
  }
  
  @Test
  public void table_nested_brackets_open() throws Exception {
    runInvalidTest("table-nested-brackets-open");
  }

  @Test
  public void empty_implicit_table() {
    runInvalidTest("empty-implicit-table");
  }

  @Test
  public void empty_table() throws Exception {
    runInvalidTest("empty-table");
  }

  @Test
  public void array_mixed_types_ints_and_floats() throws Exception {
    runInvalidTest("array-mixed-types-ints-and-floats");
  }

  @Test
  public void array_mixed_types_arrays_and_ints() throws Exception {
    runInvalidTest("array-mixed-types-arrays-and-ints");
  }
  
  @Test
  public void array_mixed_types_strings_and_ints() throws Exception {
    runInvalidTest("array-mixed-types-strings-and-ints");
  }

  @Test
  public void datetime_malformed_no_leads() throws Exception {
    runInvalidTest("datetime-malformed-no-leads");
  }

  @Test
  public void datetime_malformed_no_secs() throws Exception {
    runInvalidTest("datetime-malformed-no-secs");
  }

  @Test
  public void datetime_malformed_no_t() throws Exception {
    runInvalidTest("datetime-malformed-no-t");
  }

  @Test
  public void datetime_malformed_no_z() throws Exception {
    runInvalidTest("datetime-malformed-no-z");
  }

  @Test
  public void datetime_malformed_with_milli() throws Exception {
    runInvalidTest("datetime-malformed-with-milli");
  }
  
  @Test
  public void duplicate_key_table() throws Exception {
    runInvalidTest("duplicate-key-table");
  }
  
  @Test
  public void duplicate_keys() throws Exception {
    runInvalidTest("duplicate-keys");
  }
  
  @Test
  public void duplicate_tables() throws Exception {
    runInvalidTest("duplicate-tables");
  }

  @Test
  public void float_no_leading_zero() throws Exception {
    runInvalidTest("float-no-leading-zero");
  }
  
  @Test
  public void float_no_trailing_digits() throws Exception {
    runInvalidTest("float-no-trailing-digits");
  }

  @Test
  public void text_after_array_entries() throws Exception {
    runInvalidTest("text-after-array-entries");
  }

  @Test
  public void text_after_integer() throws Exception {
    runInvalidTest("text-after-integer");
  }

  @Test
  public void text_after_string() throws Exception {
    runInvalidTest("text-after-string");
  }

  @Test
  public void text_after_table() throws Exception {
    runInvalidTest("text-after-table");
  }

  @Test
  public void text_before_array_separator() throws Exception {
    runInvalidTest("text-before-array-separator");
  }

  @Test
  public void text_in_array() throws Exception {
    runInvalidTest("text-in-array");
  }
  
  @After
  public void after() throws IOException {
    inputToml.close();
  }

  private void runInvalidTest(String testName) {
    inputToml = getClass().getResourceAsStream("burntsushi/invalid/" + testName + ".toml");

    try {
      new Toml().read(inputToml);
      Assert.fail("Should have rejected invalid input!");
    } catch (IllegalStateException e) {
      // success
    }
  }
}
