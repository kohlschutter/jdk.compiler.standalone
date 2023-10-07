/*
 * Copyright (c) 1996, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.kohlschutter.jdk.standaloneutil.misc;

/**
 * Smaller/stubbed version of {@code jdk.internal.misc.VM}.
 * 
 * @author Christian Kohlschütter
 */
public class VM {
  /**
   * Returns the VM arguments for this runtime environment.
   *
   * @implNote The HotSpot JVM processes the input arguments from multiple sources in the following
   *           order: 1. JAVA_TOOL_OPTIONS environment variable 2. Options from JNI Invocation API
   *           3. _JAVA_OPTIONS environment variable
   *
   *           If VM options file is specified via -XX:VMOptionsFile, the vm options file is read
   *           and expanded in place of -XX:VMOptionFile option.
   */
  public static String[] getRuntimeArguments() {
    System.err.println(
        "WARNING: VM.getRuntimeArguments is not yet implemented, returning empty array!");
    return new String[0]; // FIXME
  }
}
