/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IOUtil {

  public static String getResourceAsString(String resource) {
    StringBuilder sb = new StringBuilder();
    try {
      Files.readAllLines(Paths.get(IOUtil.getResource(resource)))
          .forEach(s -> sb.append(s).append('\n'));
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

  public static URI getResource(String resource) {
    try {
      return IOUtil.class.getClassLoader().getResource(resource).toURI();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return URI.create("");
  }
}
