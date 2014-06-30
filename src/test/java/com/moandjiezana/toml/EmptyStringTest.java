package com.moandjiezana.toml;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.File;

public class EmptyStringTest {
    @Test
    public void should_parse_multiple_sections() {
        Toml toml = new Toml().parse("str1 = \"\"\nstr2 = \"Hello, world!\"");
        assertEquals("", toml.getString("str1"));
        assertEquals("Hello, world!", toml.getString("str2"));
    }
}
