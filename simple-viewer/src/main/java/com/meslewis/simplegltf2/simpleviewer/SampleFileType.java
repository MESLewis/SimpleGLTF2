/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;

public enum SampleFileType {
  ALL("", ""),
  GLTF_STANDARD(".gltf", "glTF"),
  GLB(".glb"),
  GLTF_EMBEDDED(".gltf", "glTF-Embedded");


  private final String fileExtension;
  private final String parentFolder;

  SampleFileType(String fileExtension) {
    this(fileExtension, "");
  }

  SampleFileType(String fileExtension, String parentFolder) {
    this.fileExtension = fileExtension;
    this.parentFolder = parentFolder;
  }

  public boolean filter(File file) {
    return (
        file.getName().endsWith(this.fileExtension)
            && file.getParent().endsWith(this.parentFolder)
            && file.isFile() //Filter out folders
    );
  }

}
