/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * Targets an animation's sampler at a node's property.
 */
public class GLTFChannel extends GLTFProperty {

  /**
   * The index of a sampler in this animation used to compute the value for the target, e.g., a
   * node's translation, rotation, or scale (TRS).
   */
  @JsonProperty("sampler")
  @NotNull
  private int indexAnimationSampler = -1;

  /**
   * The index of the node and TRS property to target.
   */
  @JsonProperty("target")
  @NotNull
  private GLTFAnimationChannelTarget target;

  public Integer getAnimationSamplerIndex() {
    return indexAnimationSampler;
  }

  public GLTFAnimationChannelTarget getTarget() {
    return target;
  }
}
