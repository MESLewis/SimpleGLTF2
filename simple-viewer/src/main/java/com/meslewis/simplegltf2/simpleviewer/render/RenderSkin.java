/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFSkin;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.joml.Matrix4f;

public class RenderSkin {

  private final Set<RenderNode> joints;
  private final RenderNode skeletonRootNode;
  private final GLTFAccessor ibmAccessor;
  private final List<Matrix4f> jointMatrices;
  private final List<Matrix4f> jointNormalMatrices;

  public RenderSkin(GLTFSkin skin) {
//    skin.getSkeletonRootNode()
    this.jointMatrices = new ArrayList<>();
    this.jointNormalMatrices = new ArrayList<>();
    this.skeletonRootNode = RenderNode.from(skin.getSkeletonRootNode()).orElseThrow();
    this.ibmAccessor = skin.getInverseBindMatricesAccessor();
    this.joints = skin.getJoints().stream()
        .map(RenderNode::from)
        .map(Optional::get)
        .collect(Collectors.toSet());
  }

  public void computeJoints() {
    int i = 0;
    for (RenderNode joint : joints) {
      if (i <= jointMatrices.size()) {
        jointMatrices.add(new Matrix4f());
      }
      var jointm = jointMatrices.get(i);

      i++;
    }
  }

  public int getJointCount() {
    return this.joints.size();
  }

}
