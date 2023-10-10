# Standalone jdk.compiler

## What

This repository builds standalone jdk.compiler artifacts that can be used just
like other regular Maven dependencies.

## Why

### Motivation

A typical way of using the Java Compiler API is to rely on the presence of such
API in the Java VM that runs the code requiring it.

Starting with Java 16, using the Java Compiler API requires a series of
`--add-opens` incantations to the running VM. The reason is clear: Eventually,
we cannot rely on the presence of said API in the JDK, since it is an internal
component that we really shouldn't touch.

Since the Compiler API is licensed as GPLv2+Classpath-Exception, let's make it
a separate component that we can use regardless of what's available in the
current VM.

### Benefits

With this standalone compiler, you can rely on all Java 11 javac features to be available,
even when using newer Java versions.

Specifically, this is allows you to:

- compile code even if your Java environment isn't a full JDK
- target Java 1.7 for compilation without any warnings or restrictions.
- use the Compiler Tree API without resorting to `--add-opens` trickery that may eventually fail
  in newer Java releases

### Examples

See this [jsweet fork](https://github.com/kohlschutter/jsweet), where we make exhaustive use of
the Compiler Tree API.

## How

### Usage

First, add the following Maven dependency to your project:

```xml
    <dependency>
        <groupId>com.kohlschutter.jdk.compiler</groupId>
        <artifactId>standalone-jdk11</artifactId>
        <version>1.0.0</version>
    </dependency>
```

If your project is modularized, also add the following statements to your `module-info.java`:

```
  requires standalone.jdk.compiler;
  requires com.kohlschutter.jdk.standaloneutil;
```

This gives you access to all `com.sun.tools.*` and `com.sun.source.*` packages.

Then change your code to use `standalone.com.sun.`... instead of `com.sun.`..., for example
use `standalone.com.sun.tools.javac.api.JavacTool` instead of `com.sun.tools.javac.api.JavacTool`.

If you use `javax.tools.ToolProvider.getSystemJavaCompiler()`, use our own version of it instead:
`com.kohlschutter.jdk.standaloneutil.ToolProvider.getSystemJavaCompiler()` (or just change the
`import` statement).

The standalone compiler uses its own copies for both `lib/modules` contents as well as `lib/ct.sym`
from a recent JDK 11 java home directory, which is automatically included via the
`com.kohlschutter.jdk:standalone-home` dependency.

### Project setup and structure

The code in this repository relies on copies of the "jdk.compiler" code
obtained from Java JDKs. That code is in a separate repository, and added as
submodules to this project.

Each submodule refers to a different JDK version, which allows the creation of
multiple Maven artifacts, one for each major JDK version.

## Who

This repository has been packaged by Christian Kohlschütter.

The code itself carries the original license, GNU General Public License
version 2 only, subject to the "Classpath" exception as provided in
the LICENSE file that accompanies this code.
