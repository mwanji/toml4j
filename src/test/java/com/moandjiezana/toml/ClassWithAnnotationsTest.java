package com.moandjiezana.toml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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

    // Print the whole file content to log
    final BufferedReader r = new BufferedReader(new FileReader(file));
    System.out.println("--Beginning of file--");
    while (true) {
      final String line = r.readLine();
      if (line == null) break;
      System.out.println(line);
    }
    System.out.println("--End of file--");
  }

  private static class FirstAnnotated {
    @TomlIgnore
    private String ignoredField;
    @TomlComment("This toggles debug features")
    private boolean enableDebugMode = false;
    @TomlComment({"Delay between messages", "Can not be negative"})
    private int delay = 12;
    @TomlComment("Subclass containing more configuration options")
    private SecondAnnotated subclass = new SecondAnnotated();

    private static class SecondAnnotated {
      @TomlComment("An list of words which should not be visible in log")
      private String[] blacklistedWords = new String[]{"test", "test2"};
      @TomlComment("The prefix to be shown when sending log messages\nDefaults to \"[Test]\"")
      private String logPrefix = "[Test]";
      @TomlComment("Even more options")
      private ThirdAnnotated third = new ThirdAnnotated();

      private static class ThirdAnnotated {
        @TomlComment("Max length of messages")
        private int maxLength = Integer.MAX_VALUE / 2;
      }
    }
  }
}
