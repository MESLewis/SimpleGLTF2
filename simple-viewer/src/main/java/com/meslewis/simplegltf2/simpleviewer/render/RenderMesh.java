/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import com.meslewis.simplegltf2.data.GLTFNode;

public class RenderMesh extends RenderNode {
  private final float[] weights;

  public RenderMesh(GLTFNode node, RenderNode parent) {
    super(node, parent);
    weights = node.getMesh().orElseThrow().getWeights().clone();
  }

  public float[] getWeights() {
    return weights;
  }
}
