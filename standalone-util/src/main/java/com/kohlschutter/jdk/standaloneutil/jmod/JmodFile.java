/*
 * Copyright (c) 2016, 2017, Oracle and/or its affiliates. All rights reserved.
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
package com.kohlschutter.jdk.standaloneutil.jmod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Smaller version of {@code jdk.internal.jmod.JModFile}.
 * 
 * @author Christian Kohlschütter
 */
public class JmodFile {
//jmod magic number and version number
  private static final int JMOD_MAJOR_VERSION = 0x01;
  private static final int JMOD_MINOR_VERSION = 0x00;
  private static final byte[] JMOD_MAGIC_NUMBER = {
      0x4A, 0x4D, /* JM */
      JMOD_MAJOR_VERSION, JMOD_MINOR_VERSION, /* version 1.0 */
  };

  public static void checkMagic(Path file) throws IOException {
      try (InputStream in = Files.newInputStream(file)) {
          // validate the header
          byte[] magic = in.readNBytes(4);
          if (magic.length != 4) {
              throw new IOException("Invalid JMOD file: " + file);
          }
          if (magic[0] != JMOD_MAGIC_NUMBER[0] ||
              magic[1] != JMOD_MAGIC_NUMBER[1]) {
              throw new IOException("Invalid JMOD file: " + file.toString());
          }
          if (magic[2] > JMOD_MAJOR_VERSION ||
              (magic[2] == JMOD_MAJOR_VERSION && magic[3] > JMOD_MINOR_VERSION)) {
              throw new IOException("Unsupported jmod version: " +
                  magic[2] + "." + magic[3] + " in " + file.toString());
          }
      }
  }
}
