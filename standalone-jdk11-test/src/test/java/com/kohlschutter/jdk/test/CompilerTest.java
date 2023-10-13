package com.kohlschutter.jdk.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import standalone.com.sun.tools.javac.main.JavaCompiler;

public class CompilerTest {
  @Test
  public void testCompilerVersion() throws Exception {
    assertEquals("11", JavaCompiler.version().split("\\.")[0]);
  }

  private void testCompileSimple(String version, boolean release, int expectedRc)
      throws IOException {
    Path tmpDir = Files.createTempDirectory("standaloneJavacTest");
    Path helloJava = tmpDir.resolve("Hello.java");
    Path helloClass = tmpDir.resolve("Hello.class");
    try {
      try (PrintWriter pw = new PrintWriter(new FileOutputStream(helloJava.toFile()), true,
          StandardCharsets.UTF_8)) {
        pw.println("public class Hello {");
        pw.println("  public static void main(String[] args) throws Exception {");
        pw.println("    System.out.println(\"Hello World\");");
        pw.println("  }");
        pw.println("}");
      }

      System.out.println(tmpDir);

      List<String> args = new ArrayList<>();
      if (version != null) {
        if (release) {
          args.addAll(List.of("--release", version));
        } else {
          args.addAll(List.of("-source", version, "-target", version));
        }
      }
      args.add(helloJava.toAbsolutePath().toString());

      int rc = standalone.com.sun.tools.javac.Main.compile(args.toArray(new String[0]));
      assertEquals(expectedRc, rc);

      if (rc == 0) {
        assertTrue(Files.exists(helloClass));
      } else {
        assertFalse(Files.exists(helloClass));
      }
    } finally {
      Files.deleteIfExists(helloClass);
      Files.deleteIfExists(helloJava);
      Files.delete(tmpDir);
    }
  }

  @Test
  public void testCompileDefault() throws Exception {
    testCompileSimple(null, false, 0);
  }

  @Test
  public void testCompileJava7() throws Exception {
    testCompileSimple("1.7", false, 0);
  }

  @Test
  public void testCompileJava8() throws Exception {
    testCompileSimple("1.8", false, 0);
  }

  @Test
  public void testCompileJava11() throws Exception {
    testCompileSimple("11", true, 0);
  }

  @Test
  public void testCompileJava12() throws Exception {
    testCompileSimple("12", true, 2); // not supported
  }
}
