/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IOUtil {

  private static final String resourceAbsolutePath = new File("src/main/resources")
      .getAbsolutePath();
  private static final URI resourceAbsoluteURI = new File(resourceAbsolutePath).toURI();

  public static String getResouce(String path) {
    StringBuilder sb = new StringBuilder();
    try {
      Files.lines(Paths.get(getResourceAbsolutePath() + path))
          .forEach(s -> sb.append(s + "\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  static void getAllFileChildren(File file, List<File> retList) {
    if (file != null) {
      retList.add(file);
      if (file.isDirectory()) {
        File[] files = file.listFiles();
        if (files != null) {
          Arrays.stream(files).filter(Objects::nonNull)
              .forEach(file1 -> getAllFileChildren(file1, retList));
        }
      }
    }
  }

  public static String getResourceAbsolutePath() {
    return resourceAbsolutePath;
  }

  public static URI getResourceAbsoluteURI() {
    return resourceAbsoluteURI;
  }
}
