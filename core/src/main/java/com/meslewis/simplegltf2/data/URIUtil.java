/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

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
   */
  public static ByteBuffer getDirectBufferFromGeneralURI(GLTF base, URI path) {
    if (path != null && path.getScheme() != null) {
      return dataURIToDirectBuffer(path);
    } else {
      return pathURIToDirectBuffer(base, path);
    }
  }

  /**
   * GLTF data uri's are always base 64 encoded
   *
   * @param uri
   * @return
   */
  private static ByteBuffer dataURIToDirectBuffer(URI uri) {
    Base64.Decoder decoder = Base64.getMimeDecoder();
    String encodedData = uri.getSchemeSpecificPart();
    // ',' is always the last character before the data
    encodedData = encodedData.substring(encodedData.indexOf(',') + 1);
    ByteBuffer buf = ByteBuffer.allocateDirect(encodedData.length()).order(ByteOrder.LITTLE_ENDIAN);
    return buf.put(decoder.decode(encodedData.getBytes())).rewind();
  }

  /**
   * Resolve a URI path relative to initial GLTF file to a stream
   *
   * @param gltf
   * @param path
   * @return
   */
  private static ByteBuffer pathURIToDirectBuffer(GLTF gltf, URI path) {
    URI resolvedURI = null;
    if (path != null) {
      resolvedURI = gltf.resolveURI(path.getPath());
    }
    return gltf.getGltfImporter().getDirectByteBuffer(resolvedURI);
  }
}
