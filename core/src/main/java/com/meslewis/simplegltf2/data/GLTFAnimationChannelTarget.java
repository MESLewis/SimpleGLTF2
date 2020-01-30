/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javax.validation.constraints.NotNull;

/**
 * The index of the node and TRS property that an animation channel targets.
 */
public class GLTFAnimationChannelTarget extends GLTFProperty {

  /**
   * The index of the node to target.
   */
  @JsonProperty("node")
  private Integer indexNode;

  /**
   * The name of the node's TRS property to modify, or the \
   */
  @JsonProperty("path")
  @NotNull
  private GLTFPath path;

  @JsonSetter("path")
  void setPath(String string) {
    this.path = GLTFPath.valueOf(string.toUpperCase());
  }

  public GLTFNode getNode() {
    return gltf.getNode(indexNode).orElse(null);
  }

  public GLTFPath getPath() {
    return path;
  }
}
