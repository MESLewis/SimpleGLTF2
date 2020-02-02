/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import com.meslewis.simplegltf2.data.GLTF;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class GLTFImporterTest {

  @TestFactory
  public Collection<DynamicTest> testKhronosModelsGLTFDeserialization() {
    //Tests loading .gltf and .glb files from Khronos glTF-sample-Models repo
    URI uri = new File("").toURI().resolve("../sample-models/glTF-Sample-Models/2.0/");

    ArrayList<File> allFiles = new ArrayList<>();

    getAllFileChildren(new File(uri), allFiles);

    return allFiles.stream()
        .filter(file -> file.getName().endsWith(".gltf") || file.getName().endsWith(".glb"))
        .map(file -> DynamicTest.dynamicTest(
            "Deserialize file: " + file.getName(),
            () -> {
              assert (importGLTF(file) != null);
            }))
        .collect(Collectors.toList());
  }

  private GLTF importGLTF(File file) {
    return GLTFImporter.instance.load(file.toURI());
  }

  private void getAllFileChildren(File file, List<File> retList) {
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
}