package de.thelooter.toml;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorMessagesTest {


    @Test
    public void invalid_table() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[in valid]");
        });

        assertEquals("Invalid table definition on line 1: [in valid]]", exception.getMessage());
    }

    @Test
    public void duplicate_table() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[again]\n[again]");
        });

        assertEquals("Duplicate table definition on line 2: [again]", exception.getMessage());
    }

    @Test
    public void empty_implicit_table_name() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[a..b]");
        });

        assertEquals("Invalid table definition due to empty implicit table name: [a..b]", exception.getMessage());

    }

    @Test
    public void duplicate_key() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("k = 1\n  k = 2");
        });

        assertEquals("Duplicate key on line 2: k", exception.getMessage());
    }

    @Test
    public void invalid_key() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("k\" = 1");
        });

        assertEquals("Key is not followed by an equals sign on line 1: k\" = 1", exception.getMessage());
    }

    @Test
    public void invalid_table_array() {

        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[[in valid]]");
        });

        assertEquals("Invalid table array definition on line 1: [[in valid]]", exception.getMessage());
    }

    @Test
    public void invalid_value() {

        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("k = 1 t");
        });

        assertEquals("Invalid text after key k on line 1. Make sure to terminate the value or add a comment (#).",
                exception.getMessage());
    }

    @Test
    public void unterminated_multiline_literal_string() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("k = '''abc"));

        assertEquals("Unterminated value on line 1: k = '''abc", exception.getMessage());
    }

    @Test
    public void unterminated_multiline_string() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("k = \"\"\"abc\"\""));

        assertEquals("Unterminated value on line 1: k = \"\"\"abc\"\"", exception.getMessage());
    }

    @Test
    public void unterminated_array() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("k = [\"abc\""));

        assertEquals("Unterminated value on line 1: k = [\"abc\"", exception.getMessage());
    }

    @Test
    public void unterminated_inline_table() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("k = { a = \"abc\""));

        assertEquals("Unterminated value on line 1: k = { a = \"abc\"", exception.getMessage());
    }

    @Test
    public void key_without_equals() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("\nk\n=3"));

        assertEquals("Key is not followed by an equals sign on line 2: k", exception.getMessage());
    }

    @Test
    public void heterogeneous_array() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("k = [ 1,\n  1.1 ]"));

        assertEquals("k becomes a heterogeneous array on line 2", exception.getMessage());
    }

    @Test
    public void key_in_root_is_overwritten_by_table() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("a=1\n  [a]"));

        assertEquals("Key already exists for table defined on line 2: [a]", exception.getMessage());
    }

    @Test
    public void table_is_overwritten_by_key() {
        Exception exception = assertThrows(Exception.class, () -> new Toml().read("[a.b]\n  [a]\n  b=1"));

        assertEquals("Table already exists for key defined on line 3: b", exception.getMessage());
    }

    @Test
    public void should_display_correct_line_number_with_literal_multiline_string() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[table]\n\n k = '''abc\n\ndef\n'''\n # comment \n j = 4.\n l = 5");
        });

        assertEquals("Invalid value on line 8: j = 4.", exception.getMessage());
    }

    @Test
    public void should_display_correct_line_number_with_multiline_string() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[table]\n\n k = \"\"\"\nabc\n\ndef\n\"\"\"\n # comment \n j = 4.\n l = 5");
        });

        assertThat(exception.getMessage(), containsString("on line 9"));
    }

    @Test
    public void should_display_correct_line_number_with_array() {
        Exception exception = assertThrows(Exception.class, () -> {
            new Toml().read("[table]\n\n k = [\"\"\"\nabc\n\ndef\n\"\"\"\n, \n # comment \n j = 4.,\n l = 5\n]");
        });

        assertThat(exception.getMessage(), containsString("on line 10"));

    }
}
