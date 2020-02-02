/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.meslewis.simplegltf2.data.GLTF;
import com.meslewis.simplegltf2.defaultImplementation.DefaultBufferIO;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLTFImporter {

  private static final Logger logger = LoggerFactory.getLogger(GLTFImporter.class);
  /**
   * Default GLTFImporter instance
   */
  public static final GLTFImporter instance = new GLTFImporter();

  private BufferIO bufferIO = new DefaultBufferIO();

  private ObjectMapper mapper = new ObjectMapper();

  public GLTF load(URI uri) {
    try {
      InputStream jsonStream;

      if (uri.toString().endsWith(".glb")) {
        logger.info("Loading .glb file: " + uri.toString());
        GLBLoader glbLoader = new GLBLoader(this);
        glbLoader.procesGLB(uri);
        jsonStream = new ByteBufferBackedInputStream(
            glbLoader.jsonData().order(ByteOrder.LITTLE_ENDIAN));
      } else {
        jsonStream = new ByteBufferBackedInputStream(
            bufferIO.getDirectByteBuffer(uri).order(ByteOrder.LITTLE_ENDIAN));
      }

      GLTF gltf = new GLTF(this, uri);
      ObjectReader reader = mapper.setInjectableValues(injectGLTF(gltf)).readerForUpdating(gltf);
      reader.readValue(jsonStream);
      return gltf;
    } catch (Exception e) {
      logger.error("Error loading gltf file: " + uri.toString());
      logger.error(e.getLocalizedMessage());
      return null;
    }
  }

  /**
   * Inject reference to base GLTF into each node, used for index resolution Also a
   * RelativePathResolver, called to get a stream from a path
   *
   * @param gltf
   * @return
   */
  private InjectableValues injectGLTF(GLTF gltf) {
    InjectableValues.Std iv = new InjectableValues.Std();
    iv.addValue(GLTF.class, gltf);
    return iv;
  }

  public void setBufferIO(BufferIO bufferIO) {
    this.bufferIO = bufferIO;
  }

  BufferIO getBufferIO() {
    return this.bufferIO;
  }

  /**
   * Route bufferIO function through this to ensure little endian.
   *
   * @param uri
   * @return
   */
  public ByteBuffer getDirectByteBuffer(URI uri) {
    return bufferIO.getDirectByteBuffer(uri).order(ByteOrder.LITTLE_ENDIAN).rewind();
  }
}
