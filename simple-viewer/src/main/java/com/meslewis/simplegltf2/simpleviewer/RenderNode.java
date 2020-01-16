/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFNode;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderNode {

  private GLTFNode gltfNode;
  private Vector3f scale = new Vector3f();
  private Vector3f translation = new Vector3f();
  private AxisAngle4f rotation = new AxisAngle4f();
  private Matrix4f worldTransform = new Matrix4f();
  private Matrix4f inverseWorldTransform = new Matrix4f();
  private Matrix4f normalMatrix = new Matrix4f();

  public RenderNode(GLTFNode node, RenderNode parent) {
    this.gltfNode = node;
    if (node != null) {
      if (node.getMatrix() != null) {
        applyMatrix(node.getMatrix());
      } else {
        float[] scalef = node.getScale();
        scale = new Vector3f().set(scalef[0], scalef[1], scalef[2]);

        float[] rotf = node.getRotation();
        rotation = new AxisAngle4f().set(rotf[1], rotf[2], rotf[3], rotf[0]);

        float[] traf = node.getTranslation();
        translation = new Vector3f().set(traf[0], traf[1], traf[2]);
      }
    }

    //Apply transform relative to parent
    if (parent != null) {
      applyTransform(parent.getWorldTransform());
    } else {
      applyTransform(new Matrix4f());
    }
  }

  private void applyMatrix(float[] floatMatrix) {
    Matrix4f matrix = new Matrix4f().set(floatMatrix);
    matrix.getScale(scale);
    matrix.getTranslation(translation);
    matrix.getRotation(rotation);
  }

  private Matrix4f getLocalTransform() {
    return new Matrix4f().rotation(rotation).translate(translation).scale(scale);
  }

  public Matrix4f getWorldTransform() {
    return this.worldTransform;
  }

  private void applyTransform(Matrix4f parentTransform) {
    getLocalTransform().mul(parentTransform, worldTransform);
    worldTransform.invert(inverseWorldTransform);
    inverseWorldTransform.transpose(normalMatrix);
  }

  public GLTFNode getGltfNode() {
    return gltfNode;
  }

  public Matrix4f getNormalMatrix() {
    return normalMatrix;
  }
}
