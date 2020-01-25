/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A perspective camera containing properties to create a perspective projection matrix.
 */
public class GLTFPerspective {

  /**
   * The floating-point aspect ratio of the field of view. When this is undefined, the aspect ratio
   * of the canvas is used.
   */
  @JsonProperty("aspectRatio")
  private Float aspectRatio;

  /**
   * The floating-point vertical field of view in radians.
   */
  @JsonProperty("yfov")
  @Min(0)
  @NotNull
  private Float yfov;

  /**
   * The floating-point distance to the far clipping plane. When defined, `zfar` must be greater
   * than `znear`. If `zfar` is undefined, runtime must use infinite projection matrix. TODO
   * infinite projection matrix
   */
  @JsonProperty("zfar")
  @Min(0)
  private Float zfar;

  /**
   * The floating-point distance to the near clipping plane.
   */
  @JsonProperty("znear")
  @Min(0)
  @NotNull
  private Float znear;

  public Float getAspectRatio() {
    return aspectRatio;
  }

  public Float getYfov() {
    return yfov;
  }

  public Optional<Float> getZfar() {
    return Optional.ofNullable(zfar);
  }

  public Optional<Float> getZnear() {
    return Optional.ofNullable(znear);
  }
}
