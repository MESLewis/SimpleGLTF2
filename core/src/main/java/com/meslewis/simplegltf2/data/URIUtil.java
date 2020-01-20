/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIUtil {

  private static final Logger logger = LoggerFactory.getLogger(URIUtil.class);

  /**
   * Handles getting a general stream from any URI contained in a GLTF file. Supports relative and
   * absolute URI's
   * <p>
   * This method is untested
   * <p>
   * TODO make a test for this iterating over valid GLTF URI usage
   */
  static InputStream getStreamFromGeneralURI(GLTF base, URI path) {
    if(path.getScheme() != null) {
      return dataURIToStream(path);
    } else {
      return pathURIToStream(base, path);
    }
  }

  /**
   * GLTF data uri's are always base 64 encoded
   *
   * @param uri
   * @return
   */
  private static InputStream dataURIToStream(URI uri) {
    Base64.Decoder decoder = Base64.getMimeDecoder();
    String encodedData = uri.getSchemeSpecificPart();
    // ',' is always the last character before the data
    encodedData = encodedData.substring(encodedData.indexOf(',') + 1);
    InputStream encodedStream = new ByteArrayInputStream(encodedData.getBytes());
    return decoder.wrap(encodedStream);
  }

  /**
   * Resolve a URI path relative to initial GLTF file to a stream
   *
   * @param gltf
   * @param path
   * @return
   */
  private static InputStream pathURIToStream(GLTF gltf, URI path) {
    URI resolvedURI = null;
    if (path != null) {
      resolvedURI = gltf.resolveURI(path.getPath());
    }
    return gltf.getInputStream(resolvedURI);
  }

  public static ByteBuffer readStreamToDirectBuffer(InputStream stream) throws IOException {
    logger.info("Reading stream into direct byte buffer");
    byte[] allBytes = stream.readAllBytes();
    ByteBuffer buffer = ByteBuffer.allocateDirect(allBytes.length);
    buffer.put(allBytes).order(ByteOrder.LITTLE_ENDIAN)
        .rewind();//I think this can cause jvm crashes down the line
    return buffer;
  }
}
