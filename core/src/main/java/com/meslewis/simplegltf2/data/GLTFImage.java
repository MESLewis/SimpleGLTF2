/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Image data used to create a texture. Image can be referenced by URI or `bufferView` index.
 * `mimeType` is required in the latter case.
 */
public class GLTFImage extends GLTFChildOfRootProperty {

  /**
   * The uri of the image.  Relative paths are relative to the .gltf file.  Instead of referencing
   * an external file, the uri can also be a data-uri.  The image format must be jpg or png.
   */
  @JsonProperty("uri")
  private URI uri;

  /**
   * The image's MIME type. Required if `bufferView` is defined.
   * <p>
   * TODO either "image/jpeg" or "image/png"
   */
  @JsonProperty("mimeType")
  private String mimeType;

  /**
   * The index of the bufferView that contains the image. Use this instead of the image's uri
   * property.
   */
  @JsonProperty("bufferView")
  private Integer indexBufferView;

  /**
   * Returns the data for this image in a buffer.
   *
   * @return TODO
   */
  public ByteBuffer getDirectByteBuffer() {
    ByteBuffer imgBuffer = null;
    if (indexBufferView != null) {
      try {
        GLTFBufferView bv = gltf.getBufferView(indexBufferView);
        imgBuffer = bv.getData(0, bv.getByteLength());
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.info("Image data from URI");
      imgBuffer = URIUtil.getDirectBufferFromGeneralURI(gltf, uri);
    }
    return imgBuffer;
  }

  public String getMimeType() {
    if (this.mimeType == null) {
      if (getURI() != null && getURI().getScheme() != null && getURI().getScheme()
          .contains("data")) { //Data URI
        this.mimeType = getURI().getSchemeSpecificPart();
        this.mimeType = mimeType.substring(0, mimeType.indexOf(';'));
      } else { //Extract a good guess from the file name
        this.mimeType = "image/" + uri.getPath().substring(uri.getPath().lastIndexOf('.'));
      }
    }
    return mimeType;
  }

  public URI getURI() {
    return this.uri;
  }
}
