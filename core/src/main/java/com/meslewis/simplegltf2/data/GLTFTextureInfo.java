/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Reference to a texture.
 */
public class GLTFTextureInfo extends GLTFProperty {

  /**
   * The index of the texture.
   */
  @JsonProperty("index")
  @NotNull
  private Integer indexTexture;

  /**
   * This integer value is used to construct a string in the format `TEXCOORD_<set index>` which is
   * a reference to a key in mesh.primitives.attributes (e.g. A value of `0` corresponds to
   * `TEXCOORD_0`). Mesh must have corresponding texture coordinate attributes for the material to
   * be applicable to it.
   */
  @JsonProperty("texCoord")
  @Min(0)
  private Integer texCoord = 0;

  public String getSamplerName() {
    return "TEXCOORD_" + texCoord;
  }

  public Integer getTexCoord() {
    return this.texCoord;
  }

  public GLTFTexture getTexture() {
    return gltf.getTexture(indexTexture);
  }
}
