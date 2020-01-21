/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import java.net.URI;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles references to within a glb files, passes others through
 */
class GLBBufferIO implements BufferIO {

  private static final Logger logger = LoggerFactory.getLogger(GLBBufferIO.class);

  private BufferIO fallback;
  private GLBLoader loader;

  GLBBufferIO(BufferIO fallback, GLBLoader loader) {
    this.fallback = fallback;
    this.loader = loader;
  }

  @Override
  public ByteBuffer getDirectByteBuffer(URI uri) {
    if (uri == null) { //Referencing the first bin chunk
      logger.debug("Getting direct buffer of .glb bin chunk.");
      return loader.binData();
    } else {
      return fallback.getDirectByteBuffer(uri);
    }
  }
}
