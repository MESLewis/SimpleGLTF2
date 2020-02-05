/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFSkin;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.joml.Matrix4f;

public class RenderSkin {

  private final Runnable jointResolver;
  private LinkedHashSet<RenderNode> joints; //Need to retain order for calculation
  private final RenderNode skeletonRootNode;
  private final GLTFAccessor ibmAccessor;
  private final List<Matrix4f> jointMatrices;
  private final List<Matrix4f> jointNormalMatrices;

  public RenderSkin(GLTFSkin skin) {
    this.jointMatrices = new ArrayList<>();
    this.jointNormalMatrices = new ArrayList<>();
    this.skeletonRootNode = RenderNode.from(skin.getSkeletonRootNode()).orElse(null);
    this.ibmAccessor = skin.getInverseBindMatricesAccessor().orElse(null);
    jointResolver = () -> {
      this.joints = skin.getJoints().stream()
          .map(RenderNode::from)
          .map(Optional::get)
          .collect(Collectors.toCollection(LinkedHashSet::new));
    };
  }

  public void computeJoints(RenderMesh mesh) {
    int i = 0;
    for (RenderNode renderNode : getJoints()) {
      if (i == jointMatrices.size()) {
        jointMatrices.add(new Matrix4f());
        jointNormalMatrices.add(new Matrix4f());
      }
      var jointMatrix = jointMatrices.get(i);
      if (ibmAccessor == null) {
        jointMatrix.identity(); //ibm accessor is optional
      } else {
        //TODO check if Matrix4fStack would be better
        jointMatrix.set(loadMatrix(i * 16)); //Inverse bind matrix for joint
      }
      renderNode.getWorldTransform().mul(jointMatrix, jointMatrix);//Global transform of joint node
      mesh.getInverseWorldTransform().mul(jointMatrix, jointMatrix);//Global transform of mesh node

      var normalMatrix = jointNormalMatrices.get(i);
      normalMatrix.set(jointMatrix);
      normalMatrix.invert();
      normalMatrix.transpose();

      i++;
    }
  }

  private Set<RenderNode> getJoints() {
    if (joints == null) {
      jointResolver.run();
    }
    return joints;
  }

  private float[] loadMatrix(int primitiveIndex) {
    float[] store = new float[16];
    for (int i = 0; i < 16; i++) {
      store[i] = ibmAccessor.getFloat(primitiveIndex++);
    }
    return store;
  }

  public int getJointCount() {
    if (joints == null) {
      jointResolver.run();
    }
    return this.joints.size();
  }

  public Matrix4f[] getJointMatrices() {
    return jointMatrices.toArray(new Matrix4f[0]);
  }

  public Matrix4f[] getJointNormalMatrices() {
    return jointNormalMatrices.toArray(new Matrix4f[0]);
  }
}
