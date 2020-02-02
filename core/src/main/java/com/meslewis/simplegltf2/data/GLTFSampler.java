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
   * (LINEAR). TODO LINEAR is an unofficial default for now
   */
  @JsonProperty("magFilter")
  private GLTFMagnificationFilter magFilter = GLTFMagnificationFilter.LINEAR;

  /**
   * Minification filter.  All valid values correspond to WebGL enums. TODO LINEAR_MIPMAP_LINEAR is
   * an unofficial default for now
   */
  @JsonProperty("minFilter")
  private GLTFMinificationFilter minFilter = GLTFMinificationFilter.LINEAR_MIPMAP_LINEAR;

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

  public GLTFMagnificationFilter getMagFilter() {
    return magFilter;
  }

  /**
   *
   */
  @JsonSetter("magFilter")
  private void setMagFilter(int value) {
    this.magFilter = GLTFMagnificationFilter.getType(value);
  }

  public GLTFMinificationFilter getMinFilter() {
    return minFilter;
  }

  /**
   *
   */
  @JsonSetter("minFilter")
  private void setMinFilter(int value) {
    this.minFilter = GLTFMinificationFilter.getType(value);
  }

  public GLTFWrap getWrapS() {
    return wrapS;
  }

  /**
   *
   */
  @JsonSetter("wrapS")
  private void setWrapS(int value) {
    this.wrapS = GLTFWrap.getType(value);
  }

  public GLTFWrap getWrapT() {
    return wrapT;
  }

  /**
   *
   */
  @JsonSetter("wrapT")
  private void setWrapT(int value) {
    this.wrapT = GLTFWrap.getType(value);
  }
}
