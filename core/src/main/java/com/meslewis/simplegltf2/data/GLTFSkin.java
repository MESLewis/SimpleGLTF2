/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 * Joints and matrices defining a skin.
 */
public class GLTFSkin extends GLTFChildOfRootProperty {

  /**
   * The index of the accessor containing the floating-point 4x4 inverse-bind matrices.  The default
   * is that each matrix is a 4x4 identity matrix, which implies that inverse-bind matrices were
   * pre-applied.
   */
  private GLTFAccessor inverseBindMatricesAccessor;
  /**
   * The index of the node used as a skeleton root. The node must be the closest common root of the
   * joints hierarchy or a direct or indirect parent node of the closest common root.
   */
  private GLTFNode skeletonRoot;
  /**
   * Indices of skeleton nodes, used as joints in this skin.  The array length must be the same as
   * the `count` property of the `inverseBindMatrices` accessor (when defined).
   */
  @NotNull
  private Set<GLTFNode> joints;

  @JsonProperty("skeleton")
  private void setSkeletonRoot(int index) {
    gltf.indexResolvers.add(() -> skeletonRoot = gltf.getNode(index));
  }

  public Optional<GLTFAccessor> getInverseBindMatricesAccessor() {
    return Optional.ofNullable(inverseBindMatricesAccessor);
  }

  @JsonProperty("inverseBindMatrices")
  private void setInverseBindMatricesAccessor(int index) {
    gltf.indexResolvers.add(() -> inverseBindMatricesAccessor = gltf.getAccessor(index));
  }

  public Optional<GLTFNode> getSkeletonRootNode() {
    return Optional.ofNullable(skeletonRoot);
  }

  public Set<GLTFNode> getJoints() {
    return joints;
  }

  @JsonProperty("joints")
  private void setJoints(Set<Integer> indexSet) {
    gltf.indexResolvers.add(() -> {
      joints = new HashSet<>();
      indexSet.forEach(index -> gltf.getNode(index));
    });
  }
}
