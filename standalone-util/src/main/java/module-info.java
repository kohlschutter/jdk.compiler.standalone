module com.kohlschutter.jdk.standaloneutil {
  exports com.kohlschutter.jdk.standaloneutil;
  exports com.kohlschutter.jdk.standaloneutil.annotation;
  exports com.kohlschutter.jdk.standaloneutil.jmod;
  exports com.kohlschutter.jdk.standaloneutil.misc;

  requires transitive java.compiler;
  uses javax.tools.JavaCompiler;
}
