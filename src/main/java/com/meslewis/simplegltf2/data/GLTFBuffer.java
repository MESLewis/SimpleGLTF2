/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A buffer points to binary geometry, animation, or skins. TODO some buffers can be copied directly
 * to GPU
 */
public class GLTFBuffer extends GLTFChildOfRootProperty {

  /**
   * The uri of the buffer. Relative paths are relative to the .gltf file. Instead of referencing an
   * external file, the uri can also be a data-uri.
   */
  @JsonProperty("uri")
  private URI uri;

  /**
   * The length of the buffer in bytes.
   */
  @JsonProperty("byteLength")
  @NotNull
  @Min(1)
  private Integer byteLength;

  public URI getUri() {
    return uri;
  }

  public Integer getByteLength() {
    return byteLength;
  }
}
