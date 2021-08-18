package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class ClassWithAnnotationsTest {
  @Rule
  public TemporaryFolder testDirectory = new TemporaryFolder();

  @Test
  public void should_support_SerializedName() throws Exception {
    File file = testDirectory.newFile();

    //Save config
    final FirstAnnotated newInst = new FirstAnnotated();
    newInst.ignoredField = "I should not be visible in the file";
    final TomlWriter w = new TomlWriter.Builder()
            .indentValuesBy(2)
            .indentTablesBy(4)
            .padArrayDelimitersBy(3)
            .build();
    w.write(newInst, file);

    //Load it back up
    final FirstAnnotated toml = new Toml().read(file).to(FirstAnnotated.class);
    assertNull(toml.ignoredField);
    assertFalse(toml.enableDebugMode);
    assertEquals(12, toml.delay);
    assertEquals("[Test]", toml.subclass.logPrefix);
    assertArrayEquals(new String[]{"test", "test2"}, toml.subclass.blacklistedWords);
    assertEquals(Integer.MAX_VALUE / 2, toml.subclass.third.maxLength);
    assertEquals(57392729L, toml.randomizer.seed);
    final String expectedOutput = "# This toggles debug features\n" +
            "enableDebugMode = false\n" +
            "# Delay between messages\n" +
            "# Can not be negative\n" +
            "delay = 12\n" +
            "\n" +
            "# Subclass containing more configuration options\n" +
            "[subclass]\n" +
            "  # An list of words which should not be visible in log\n" +
            "blacklistedWords = [   \"test\", \"test2\"   ]\n" +
            "  # The prefix to be shown when sending log messages\n" +
            "  # Defaults to \"[Test]\"\n" +
            "  logPrefix = \"[Test]\"\n" +
            "\n" +
            "    # Even more options\n" +
            "    [subclass.third]\n" +
            "      # Max length of messages\n" +
            "      maxLength = 1073741823\n" +
            "\n" +
            "# Config for the randomizer\n" +
            "[randomizer]\n" +
            "  # The seed of the randomizer\n" +
            "  seed = 57392729\n";
    assertEquals(expectedOutput, w.write(toml));
  }

  private static class FirstAnnotated {
    @TomlIgnore
    private String ignoredField;
    @TomlComment("This toggles debug features")
    private final boolean enableDebugMode = false;
    @TomlComment({"Delay between messages", "Can not be negative"})
    private final int delay = 12;
    @TomlComment("Subclass containing more configuration options")
    private final SecondAnnotated subclass = new SecondAnnotated();
    @TomlComment("Config for the randomizer")
    private final FourthAnnotated randomizer = new FourthAnnotated();


    private static class SecondAnnotated {
      @TomlComment("An list of words which should not be visible in log")
      private final String[] blacklistedWords = new String[]{"test", "test2"};
      @TomlComment("The prefix to be shown when sending log messages\nDefaults to \"[Test]\"")
      private final String logPrefix = "[Test]";
      @TomlComment("Even more options")
      private final ThirdAnnotated third = new ThirdAnnotated();

      private static class ThirdAnnotated {
        @TomlComment("Max length of messages")
        private final int maxLength = Integer.MAX_VALUE / 2;
      }
    }
  }

  private static class FourthAnnotated {
    @TomlComment("The seed of the randomizer")
    private final long seed = 57392729L;
  }
}