/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFNode;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderLight extends RenderNode {

  private Vector3f color = new Vector3f().set(1);
  private float intensity = 1;

  public RenderLight(GLTFNode node, RenderNode parent) {
    super(node, parent);
  }

  public UniformLight toUniform() {
    UniformLight uLight = new UniformLight();
    if (this.getGltfNode() != null) {
      GLTFNode node = this.getGltfNode();
      Matrix4f matrix = this.getWorldTransform();

    }
    return uLight;
  }
}
