/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtil {

  private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

  public static String getResourceAsString(String resource) {
    StringBuilder sb = new StringBuilder();
    InputStream stream = IOUtil.getResourceAsStream(resource);
    Scanner scanner = new Scanner(stream);
    while (scanner.hasNext()) {
      sb.append(scanner.nextLine()).append('\n');
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
    var url = IOUtil.class.getClassLoader().getResource(resource);
    try {
      assert url != null;
      var uri = new URI(url.toExternalForm());
      if (!uri.toString().toLowerCase().startsWith("file:")) {
        ensureFileSystemExists(uri);
      }
      return uri;
    } catch (Exception e) {
      logger.error("Error loading resource: " + resource + ": " + e);
      e.printStackTrace();
    }
    return null;
  }

  public static InputStream getResourceAsStream(String resource) {
    return IOUtil.class.getClassLoader().getResourceAsStream(resource);
  }

  public static void ensureFileSystemExists(URI uri) {
    try {
      FileSystems.getFileSystem(uri);
    } catch (FileSystemNotFoundException e) {
      try {
        FileSystems.newFileSystem(uri, Collections.EMPTY_MAP);
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
