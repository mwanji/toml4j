package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NumberTest {
  @Test
  public void should_get_number() throws Exception {
    Toml toml = new Toml().read("b = 1001");

    assertEquals(1001, toml.getLong("b").intValue());
  }

  @Test
  public void should_get_negative_number() throws Exception {
    Toml toml = new Toml().read("b = -1001");

    assertEquals(-1001, toml.getLong("b").intValue());
  }
  
  @Test
  public void should_get_number_with_plus_sign() throws Exception {
    Toml toml = new Toml().read("a = +1001\nb = 1001");

    assertEquals(toml.getLong("b"), toml.getLong("a"));
  }

  @Test
  public void should_get_integer() throws Exception {
    Toml toml = new Toml().read("b = 1001");

    assertEquals(1001, toml.getInteger("b").intValue());
  }

  @Test
  public void should_get_negative_integer() throws Exception {
    Toml toml = new Toml().read("b = -1001");

    assertEquals(-1001, toml.getInteger("b").intValue());
  }

  @Test
  public void should_get_null_integer() throws Exception {
    Toml toml = new Toml().read("b = -1001");

    assertNull(toml.getInteger("J"));
  }

  @Test
  public void should_get_default_integer() throws Exception {
    Toml toml = new Toml().read("b = -1001");

    assertEquals(74, toml.getInteger("J", 74).intValue());
  }

  @Test
  public void should_get_integer_with_plus_sign() throws Exception {
    Toml toml = new Toml().read("a = +1001\nb = 1001");

    assertEquals(toml.getInteger("b"), toml.getInteger("a"));
  }

  @Test
  public void should_get_double() throws Exception {
    Toml toml = new Toml().read("double = 5.25");

    assertEquals(5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }

  @Test
  public void should_get_negative_double() throws Exception {
    Toml toml = new Toml().read("double = -5.25");

    assertEquals(-5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }
  
  @Test
  public void should_get_double_with_a_plus_sign() throws Exception {
    Toml toml = new Toml().read("double = +5.25");

    assertEquals(5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }
  
  @Test
  public void should_get_exponent() throws Exception {
    Toml toml = new Toml().read("lower_case = 1e6\nupper_case = 2E6\nwith_plus = 5e+22\nboth_plus = +5E+22\nnegative = -2E-2\nfractional = 6.626e-34");

    assertEquals(1e6, toml.getDouble("lower_case"), 0.0);
    assertEquals(2E6, toml.getDouble("upper_case"), 0.0);
    assertEquals(5e22, toml.getDouble("with_plus"), 0.0);
    assertEquals(5e22, toml.getDouble("both_plus"), 0.0);
    assertEquals(-2e-2, toml.getDouble("negative"), 0.0);
    assertEquals(6.626D * Math.pow(10, -34), toml.getDouble("fractional"), 0.0);
  }
  
  @Test
  public void should_get_number_with_underscores() throws Exception {
    Toml toml = new Toml().read("val = 100_000_000");
    
    assertEquals(100000000L, toml.getLong("val").longValue());
  }
  
  @Test
  public void should_get_integer_with_underscores() throws Exception {
      Toml toml = new Toml().read("val = 100_000_000");

      assertEquals(100000000, toml.getInteger("val").intValue());
  }

  @Test
  public void should_get_float_with_underscores() throws Exception {
    Toml toml = new Toml().read("val = 100_000.123_456");
    
    assertEquals(100000.123456, toml.getDouble("val").doubleValue(), 0);
  }
  
  @Test
  public void should_get_exponent_with_underscores() throws Exception {
    Toml toml = new Toml().read("val = 1_5e1_00");
    
    assertEquals(15e100, toml.getDouble("val").doubleValue(), 0.0);
  }
  
  @Test
  public void should_accept_irregular_underscores() throws Exception {
    Toml toml = new Toml().read("val = 1_2_3_4_5");
    
    assertEquals(12345L, toml.getLong("val").longValue());
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_number() throws Exception {
    new Toml().read("a = 200-");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_float() throws Exception {
    new Toml().read("number = 3.14  pi");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_when_illegal_characters_after_integer() throws Exception {
    new Toml().read("number = 314  pi");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_without_leading_0() {
    new Toml().read("answer = .12345");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_negative_float_without_leading_0() {
    new Toml().read("answer = -.12345");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_with_sign_after_dot() {
    new Toml().read("answer = 1.-1");
    new Toml().read("answer = 1.+1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_without_digits_after_dot() {
    new Toml().read("answer = 1.");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_negative_float_without_digits_after_dot() {
    new Toml().read("answer = -1.");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_exponent_without_digits_after_dot() {
    new Toml().read("answer = 1.E1");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_negative_exponent_without_digits_after_dot() {
    new Toml().read("answer = -1.E1");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_exponent_with_dot_in_exponent_part() {
    new Toml().read("answer = -1E1.0");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_exponent_without_numbers_after_E() {
    new Toml().read("answer = -1E");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_exponent_with_two_E() {
    new Toml().read("answer = -1E1E1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_float_with_two_dots() {
    new Toml().read("answer = 1.1.1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_at_beginning() {
    new Toml().read("answer = _1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_at_end() {
    new Toml().read("answer = 1_");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_two_underscores_in_a_row() {
    new Toml().read("answer = 1__1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_after_minus_sign() {
    new Toml().read("answer = -_1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_after_plus_sign() {
    new Toml().read("answer = +_1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_before_dot() {
    new Toml().read("answer = 1_.1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_after_dot() {
    new Toml().read("answer = 1._1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_before_E() {
    new Toml().read("answer = 1_E1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_after_E() {
    new Toml().read("answer = 1E_1");
  }
  
  @Test(expected = IllegalStateException.class)
  public void should_fail_on_underscore_followed_by_whitespace() {
    new Toml().read("answer = _ 1");
  }
}
