/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Texture sampler properties for filtering and wrapping modes.
 */
public class GLTFSampler extends GLTFChildOfRootProperty {

  /**
   * Magnification filter.  Valid values correspond to WebGL enums: `9728` (NEAREST) and `9729`
   * (LINEAR).
   */
  @JsonProperty("magFilter")
  private GLTFMagnificationFilter magFilter;

  /**
   * Minification filter.  All valid values correspond to WebGL enums.
   */
  @JsonProperty("minFilter")
  private GLTFMinificationFilter minFilter;

  /**
   * S (U) wrapping mode.  All valid values correspond to WebGL enums.
   */
  @JsonProperty("wrapS")
  private GLTFWrap wrapS = GLTFWrap.REPEAT;

  /**
   * T (V) wrapping mode.  All valid values correspond to WebGL enums.
   */
  @JsonProperty("wrapT")
  private GLTFWrap wrapT = GLTFWrap.REPEAT;


  /**
   *
   */
  @JsonSetter("magFilter")
  void setMagFilter(Integer value) {
    this.magFilter = GLTFMagnificationFilter.getType(value);
  }

  /**
   *
   */
  @JsonSetter("minFilter")
  void setMinFilter(Integer value) {
    this.minFilter = GLTFMinificationFilter.getType(value);
  }

  /**
   *
   */
  @JsonSetter("wrapS")
  void setWrapS(Integer value) {
    this.wrapS = GLTFWrap.getType(value);
  }

  /**
   *
   */
  @JsonSetter("wrapT")
  void setWrapT(Integer value) {
    this.wrapT = GLTFWrap.getType(value);
  }

  public GLTFMagnificationFilter getMagFilter() {
    return magFilter;
  }

  public GLTFMinificationFilter getMinFilter() {
    return minFilter;
  }

  public GLTFWrap getWrapS() {
    return wrapS;
  }

  public GLTFWrap getWrapT() {
    return wrapT;
  }
}
