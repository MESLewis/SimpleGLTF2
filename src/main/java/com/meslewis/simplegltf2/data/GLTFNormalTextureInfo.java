/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GLTFNormalTextureInfo extends GLTFTextureInfo {

  /**
   * The scalar multiplier applied to each normal vector of the texture. This value scales the
   * normal vector using the formula: `scaledNormal =  normalize((<sampled normal texture value> *
   * 2.0 - 1.0) * vec3(<normal scale>, <normal scale>, 1.0))`. This value is ignored if
   * normalTexture is not specified. This value is linear.
   */
  @JsonProperty("scale")
  private Float scale = 1.0f;

  public Float getScale() {
    return scale;
  }
}
