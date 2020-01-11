/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;

/**
 * A keyframe animation.
 */
public class GLTFAnimation extends GLTFChildOfRootProperty {

  /**
   * An array of channels, each of which targets an animation's sampler at a node's property.
   * Different channels of the same animation can't have equal targets. minItems 1
   */
  @JsonProperty("channels")
  @NotNull
  private ArrayList<GLTFChannel> channels;

  /**
   * An array of samplers that combines input and output accessors with an interpolation algorithm
   * to define a keyframe graph (but not its target). minItems 1
   */
  @JsonProperty("samplers")
  @NotNull
  private ArrayList<GLTFAnimationSampler> samplers;


  public ArrayList<GLTFChannel> getChannels() {
    return channels;
  }

  public ArrayList<GLTFAnimationSampler> getSamplers() {
    return samplers;
  }
}
