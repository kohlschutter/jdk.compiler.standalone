package com.kohlschutter.jdk.standaloneutil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Provides the path for "java.home", as far as the compiler is concerned.
 */
public class JavaHomeLocator {
  private static final Path COMPILER_JAVA_HOME = pathForResourceURL();

  public static Path getPathToModules() {
    return getCompilerJavaHome().resolve("modules");
  }

  public static Path getCompilerJavaHome() {
    return COMPILER_JAVA_HOME;
  }

  public static Path getCtSymPath() {
    return getCompilerJavaHome().resolve("lib").resolve("ct.sym");
  }

  private static Path pathForResourceURL() {
    try {
      return pathForResourceURL(Class.forName("com.kohlschutter.jdk.home.JavaHome"), ".");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Could not find standalone-home jar in classpath", e);
    }
  }

  private static Path pathForResourceURL(Class<?> classRef, String name) {
    if (".".equals(name) || name.isEmpty()) {
      name = "/" + classRef.getPackage().getName().replace('.', '/') + "/";
    }

    URI uri;
    try {
      URL url = classRef.getResource(name);
      if (url == null) {
        throw new FileSystemNotFoundException("Cannot find resource " + name + " relative to "
            + classRef);
      }
      uri = url.toURI();
    } catch (URISyntaxException e) {
      throw (FileSystemNotFoundException) new FileSystemNotFoundException(e.toString()).initCause(
          e);
    }

    Path p;
    try {
      p = Path.of(uri);
    } catch (FileSystemNotFoundException e) {
      String ssp = uri.getSchemeSpecificPart();

      FileSystem fs;
      try {
        fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
      } catch (IOException e1) {
        throw (FileSystemNotFoundException) new FileSystemNotFoundException("Cannot find resource "
            + name + " relative to " + classRef).initCause(e1);
      }

      int exclp = ssp.indexOf("!/");
      p = fs.getPath(exclp != -1 ? ssp.substring(exclp + 1) : ssp);
    }

    return p;
  }
}
