package com.kohlschutter.jdk.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import standalone.com.sun.tools.javac.main.JavaCompiler;

public class CompilerTest {
  @Test
  public void testCompilerVersion() throws Exception {
    assertEquals("11", JavaCompiler.version().split("\\.")[0]);
  }

  @Test
  public void testCompileJava7() throws Exception {
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
          "-source", "1.7", //
          "-target", "1.7", //
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
