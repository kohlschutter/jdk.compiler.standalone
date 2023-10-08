package com.kohlschutter.jdk.standaloneutil;

import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

import javax.tools.JavaCompiler;

/**
 * Helper class to mimic {@link javax.tools.ToolProvider}, for the purposes of providing a
 * {@link JavaCompiler} instance
 * 
 * @author Christian Kohlschütter
 */
public class ToolProvider {
  private static final String STANDALONE_MODULE_NAME = "standalone.jdk.compiler";

  /**
   * Being an intentional misnomer, this returns a new standalone Java Compiler instance,
   * <em>not</em> the actual JVM's compiler.
   * 
   * @return A new {@link JavaCompiler} instance.
   */
  public static JavaCompiler getSystemJavaCompiler() {
    ServiceLoader<JavaCompiler> sl = ServiceLoader.load(JavaCompiler.class, ToolProvider.class
        .getClassLoader());
    for (JavaCompiler tool : sl) {
      if (STANDALONE_MODULE_NAME.equals(tool.getClass().getModule().getName())) {
        return tool;
      }
    }

    Throwable suppressed = null;
    try {
      return (JavaCompiler) Class.forName("standalone.com.sun.tools.javac.api.JavacTool")
          .getConstructor().newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      suppressed = e;
    }

    IllegalStateException ise = new IllegalStateException("Module not found: "
        + STANDALONE_MODULE_NAME);
    if (suppressed != null) {
      ise.addSuppressed(suppressed);
    }
    throw ise;
  }
}
