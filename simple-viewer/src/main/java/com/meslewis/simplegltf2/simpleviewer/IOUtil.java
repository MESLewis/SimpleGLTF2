/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IOUtil {

  public static String getResouce(String path) {
    StringBuilder sb = new StringBuilder();
    try {
      Files.lines(Paths.get(SimpleViewer.getResourceAbsolutePath() + path))
          .forEach(s -> sb.append(s + "\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static int stringHash(String string) {
    int seed = 0;
    for (var i = 0; i < string.length(); ++i) {
      seed = 31 * seed + string.charAt(i);
    }
    return seed;
  }

}
