/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 * Combines input and output accessors with an interpolation algorithm to define a keyframe graph
 * (but not its target).
 */
public class GLTFAnimationSampler extends GLTFProperty {

  /**
   * The index of an accessor containing keyframe input values, e.g., time. That accessor must have
   * componentType `FLOAT`. The values represent time in seconds with `time[0] >= 0.0`, and strictly
   * increasing values, i.e., `time[n + 1] > time[n]`.
   */
  @JsonProperty("input")
  @NotNull
  private Integer indexInput;

  /**
   * The index of an accessor containing keyframe output values. When targeting translation or scale
   * paths, the `accessor.componentType` of the output values must be `FLOAT`. When targeting
   * rotation or morph weights, the `accessor.componentType` of the output values must be `FLOAT` or
   * normalized integer. For weights, each output element stores `SCALAR` values with a count equal
   * to the number of morph targets.
   */
  @JsonProperty("output")
  @NotNull
  private Integer indexOutput;

  /**
   * Interpolation algorithm.
   */
  @JsonProperty("interpolation")
  private GLTFInterpolation interpolation = GLTFInterpolation.LINEAR;

  public Optional<GLTFAccessor> getInput() {
    return gltf.getAccessor(indexInput);
  }

  public Optional<GLTFAccessor> getOutput() {
    return gltf.getAccessor(indexOutput);
  }

  public GLTFInterpolation getInterpolation() {
    return interpolation;
  }

  /**
   * Interpretation algorithm.
   */
  public enum GLTFInterpolation {
    LINEAR,
    STEP,
    CUBICSPLINE
  }
}
