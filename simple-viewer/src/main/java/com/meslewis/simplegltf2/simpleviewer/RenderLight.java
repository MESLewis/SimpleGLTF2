/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFNode;

public class RenderLight extends RenderNode {
  private final UniformLight uLight;

  public RenderLight(GLTFNode node, RenderNode parent) {
    super(node, parent);
    uLight = new UniformLight();
  }

  public UniformLight getUniformLight() {
    return uLight;
  }
}
