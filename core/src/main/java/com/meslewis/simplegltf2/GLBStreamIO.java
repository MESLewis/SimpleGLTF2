/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * Handles references to within a glb files, passes others through
 */
public class GLBStreamIO implements StreamIO {

  private StreamIO fallback;
  private GLBLoader loader;

  GLBStreamIO(StreamIO fallback, GLBLoader loader) {
    this.fallback = fallback;
    this.loader = loader;
  }

  @Override
  public InputStream getStreamForResource(URI uri) {
    if (uri == null) { //Referencing the first bin chunk
      return new ByteBufferBackedInputStream(loader.binData());
    } else {
      return fallback.getStreamForResource(uri);
    }
  }
}
