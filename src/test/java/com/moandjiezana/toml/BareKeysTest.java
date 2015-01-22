package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BareKeysTest {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void should_fail_when_characters_outside_accept_range_are_used() throws Exception {
    exception.expect(IllegalStateException.class);
    exception.expectMessage("Invalid table definition: [~]");
    
    new Toml().parse("[~]");
  }

  @Test
  public void should_fail_on_sharp_sign_in_table_names() throws Exception {
    exception.expect(IllegalStateException.class);

    new Toml().parse("[group#]\nkey=1");
  }
  
  @Test
  public void should_fail_on_spaces_in_table_names() throws Exception {
    exception.expect(IllegalStateException.class);

    new Toml().parse("[valid  key]");
  }
}
