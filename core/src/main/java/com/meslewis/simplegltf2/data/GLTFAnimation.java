/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meslewis.simplegltf2.api.Animation;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * A keyframe animation.
 */
public class GLTFAnimation extends GLTFChildOfRootProperty implements Animation {

  /**
   * An array of channels, each of which targets an animation's sampler at a node's property.
   * Different channels of the same animation can't have equal targets. minItems 1
   */
  @JsonProperty("channels")
  @NotNull
  @Valid
  private List<GLTFChannel> channels;

  /**
   * An array of samplers that combines input and output accessors with an interpolation algorithm
   * to define a keyframe graph (but not its target). minItems 1
   */
  @JsonProperty("samplers")
  @NotNull
  @Valid
  private List<GLTFAnimationSampler> samplers;


  public List<GLTFChannel> getChannels() {
    return channels;
  }

  public List<GLTFAnimationSampler> getSamplers() {
    return samplers;
  }

  @Override
  public void advance(float totalTime) {
    //TODO
  }
}
