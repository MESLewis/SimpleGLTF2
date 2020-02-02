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
 * An orthographic camera containing properties to create an orthographic projection matrix.
 */
public class GLTFOrthographic extends GLTFProperty {

  /**
   * The floating-point horizontal magnification of the view. Must not be zero.
   */
  @JsonProperty("xmag")
  @NotNull
  private float xmag;

  /**
   * The floating-point vertical magnification of the view. Must not be zero.
   */
  @JsonProperty("ymag")
  @NotNull
  private float ymag;

  /**
   * The floating-point distance to the far clipping plane. `zfar` must be greater than `znear`.
   */
  @JsonProperty("zfar")
  @Min(0)
  @NotNull
  private float zfar;

  /**
   * The floating-point distance to the near clipping plane.
   */
  @JsonProperty("znear")
  @Min(0)
  @NotNull
  private float znear;

  public float getXmag() {
    return xmag;
  }

  public float getYmag() {
    return ymag;
  }

  public float getZfar() {
    return zfar;
  }

  public float getZnear() {
    return znear;
  }
}
