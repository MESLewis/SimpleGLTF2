/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLBLoader {

  private static final Logger logger = LoggerFactory.getLogger(GLBLoader.class);

  private enum ChunkType {
    JSON(0x4E4F534A),
    BIN(0x004E4942);

    private int type;

    ChunkType(int type) {
      this.type = type;
    }

    static ChunkType findTypeString(int type) {
      for (ChunkType eVal : ChunkType.values()) {
        if (eVal.type == type) {
          return eVal;
        }
      }
      return null;
    }
  }

  private static final byte[] magic = "glTF".getBytes();

  private Map<ChunkType, ByteBuffer> chunkBufferMap = new HashMap<>();
  private GLTFImporter importer;
  private BufferIO bufferIO;

  GLBLoader(GLTFImporter importer) {
    this.importer = importer;
    this.bufferIO = new GLBBufferIO(importer.getBufferIO(), this);
    importer.setBufferIO(bufferIO);
  }

  void procesGLB(URI uri) throws IOException {
    //12-byte preamble `header`
    //One or more `chunks` that contain JSON and binary data
    //Possible to reference external resources and other chunks

    ByteBuffer glb = importer.getDirectByteBuffer(uri);

    assert (glb.order() == ByteOrder.LITTLE_ENDIAN);

    //Read magic
    byte[] magicRead = new byte[magic.length];
    glb.get(magicRead);
    if (!Arrays.equals(magicRead, magic)) {
      logger.error("GLB file is not a valid glb file.");
      return;
    }

    //TODO check version
    int version = glb.getInt();
    int length = glb.getInt();

    try {
      while (glb.hasRemaining()) {
        int chunkLength = glb.getInt();
        ChunkType chunkType = ChunkType.findTypeString(glb.getInt());
        ByteBuffer chunkBuffer = glb.slice(glb.position(), chunkLength);
        chunkBufferMap.put(chunkType, chunkBuffer);

        glb.position(glb.position() + chunkLength);
      }
    } catch (Exception e) {
      logger.error("Error loading glb file: " + e.getLocalizedMessage());
    }
  }

  ByteBuffer jsonData() {
    return chunkBufferMap.get(ChunkType.JSON);
  }

  ByteBuffer binData() {
    return chunkBufferMap.get(ChunkType.BIN);
  }
}
