/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class URIUtil {

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
    //The java decoder doesn't like incorrect padding, so remove all padding
    String encodedData = uri.getSchemeSpecificPart().replace("=", "");
    byte[] byteArray = decoder.decode(encodedData.getBytes(StandardCharsets.UTF_8));
    return new ByteArrayInputStream(byteArray);
  }

  /**
   * Resolve a URI path relative to initial GLTF file to a stream
   *
   * @param gltf
   * @param path
   * @return
   */
  private static InputStream pathURIToStream(GLTF gltf, URI path) {
    URI resolvedURI = gltf.resolveURI(path.getPath());
    return gltf.getInputStream(resolvedURI);
  }
}
