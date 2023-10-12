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

import org.junit.jupiter.api.Test;

import standalone.com.sun.tools.javac.main.JavaCompiler;

public class CompilerTest {
  @Test
  public void testCompilerVersion() throws Exception {
    assertEquals("21", JavaCompiler.version().split("\\.")[0]);
  }

  private void testCompileSimple(String version, int expectedRc) throws IOException {
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

      int rc = standalone.com.sun.tools.javac.Main.compile(new String[] {
          "-source", version, //
          "-target", version, //
          helloJava.toAbsolutePath().toString()});
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
  public void testCompileJava7() throws Exception {
    testCompileSimple("1.7", 2); // no longer supported, so compilation should fail
  }

  @Test
  public void testCompileJava8() throws Exception {
    testCompileSimple("1.8", 0);
  }

  @Test
  public void testCompileJava21Unnamed() throws Exception {
    Path tmpDir = Files.createTempDirectory("standaloneJavacTest");
    Path helloJava = tmpDir.resolve("Hello.java");
    Path helloClass = tmpDir.resolve("Hello.class");
    try {
      try (PrintWriter pw = new PrintWriter(new FileOutputStream(helloJava.toFile()), true,
          StandardCharsets.UTF_8)) {
        // Look ma, no package
        pw.println("  void main() {");
        pw.println("    System.out.println(\"Hello World\");");
        pw.println("  }");
      }

      System.out.println(tmpDir);

      int rc = standalone.com.sun.tools.javac.Main.compile(new String[] {
          "--enable-preview", //
          "-source", "21", //
          "-target", "21", //
          helloJava.toAbsolutePath().toString()});
      assertEquals(0, rc);

      assertTrue(Files.exists(helloClass));
    } finally {
      Files.deleteIfExists(helloClass);
      Files.deleteIfExists(helloJava);
      Files.delete(tmpDir);
    }
  }
}
