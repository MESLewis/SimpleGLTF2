/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GLTFScene extends GLTFChildOfRootProperty {

  /**
   * The indices of each root node. minItems 1
   */
  @JsonProperty("nodes")
  private LinkedHashSet<Integer> indexNodes;

  /**
   * Convert indexNodes to Node objects
   *
   * @return
   */
  public List<GLTFNode> getRootNodes() {
    return indexNodes.stream()
        .map(integer -> gltf.getNode(integer).get())
        .collect(Collectors.toUnmodifiableList());
  }

  public List<GLTFNode> getAllNodesAndDescendants() {
    List<GLTFNode> nodeList = new ArrayList<>();
    getRootNodes().forEach(gltfNode -> gltfNode.addSelfAndAllDescendants(nodeList));
    return nodeList;
  }
}
