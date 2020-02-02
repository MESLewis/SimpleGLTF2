/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;
import java.net.URI;

public class ManualRunUnitTestModels {

  public static void main(String[] args) {
    URI uri = new File("").toURI()
        .resolve("sample-models/glTF-Asset-Generator/Output/Positive/");
    SimpleViewer.WIDTH = 600;
    SimpleViewer.HEIGHT = 600;
    SimpleViewer viewer = new SimpleViewer(uri, SampleFileType.ALL);
    viewer.setNextFileIndex(58);
    viewer.run();
  }
  //ALL - 58 - Instancing/Instancing_11.gltf
  //ALL - 168 - Node_NegativeScale_03.gltf - TODO nothing shows up
}
