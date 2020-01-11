/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Material Occlusion Texture Info
 */
public class GLTFOcclusionTextureInfo extends GLTFTextureInfo {

  /**
   * A scalar multiplier controlling the amount of occlusion applied. A value of 0.0 means no
   * occlusion. A value of 1.0 means full occlusion. This value affects the resulting color using
   * the formula: `occludedColor = lerp(color, color * <sampled occlusion texture value>, <occlusion
   * strength>)`. This value is ignored if the corresponding texture is not specified. This value is
   * linear.
   */
  @JsonProperty("strength")
  @Min(0)
  @Max(1)
  private Float strength = 1.0f;

  public Float getStrength() {
    return strength;
  }
}
