/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.io.File;
import java.net.URI;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Holds various manual run configurations. Does not get run in test suite.
 */
@Disabled
public class ManualRunConfigurations {

  @Test
  void loadSampleModels() {
    URI uri = new File("").toURI().resolve("../sample-models/glTF-Sample-Models/2.0/");
    SimpleViewer viewer = new SimpleViewer(uri, SampleFileType.GLTF_STANDARD);
    viewer.setNextFileIndex(29);
    viewer.run();
  }
  //Standard - 28 - Fox TODO getFloat goes oob
  //Standard - 58 - Water bottle
  //Standard - 1  - Alpha blend test
  //Standard - 24 - Damaged Helmet
  //Standard - 10 - Boombox with axis.
  //Standard - 20 - Cesium man
  //Standard - 16 - Box vertex colors
  //Standard - 58 - Vertex color test
  //Standard - 13 - Box Interleaved
  //Standard - 15 - Textured non power of two TODO resize textures if not power of two
  //Standard - 27 - Flight helmet
  //Standard - 30 - Interpolation test
  //Standard - 52 - Texture Transform Test - Texture transform extension
  //Standard - 5  - Animated triangle TODO animation
  //Standard - 3  - Morph cube
  //Standard - 45 - Simple morph
  //TODO animation

  @Test
  void loadUnitTestModels() {
    URI uri = new File("").toURI()
        .resolve("../sample-models/glTF-Asset-Generator/Output/Positive/");
    SimpleViewer viewer = new SimpleViewer(uri, SampleFileType.ALL);
    viewer.setNextFileIndex(14);
    viewer.run();
  }
  //All - 14 - Animation_NodeMisc_08.gltf TODO NPE in getRenderNodeHelper
}
