/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;

public class GLTFScene extends GLTFChildOfRootProperty {

  /**
   * The indices of each root node. minItems 1
   */
  private Set<GLTFNode> rootNodes;

  /**
   * Set of GLTFNode that are the root nodes for this GLTFScene
   */
  public Set<GLTFNode> getRootNodes() {
    return rootNodes;
  }

  @JsonProperty("nodes")
  private void setRootNodes(Set<Integer> indexSet) {
    gltf.indexResolvers.add(() -> {
      rootNodes = new HashSet<>();
      indexSet.forEach(index -> rootNodes.add(gltf.getNode(index)));
    });
  }
}
