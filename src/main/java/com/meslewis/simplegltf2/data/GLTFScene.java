/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashSet;

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
  public List<GLTFNode> getNodes() {
    return indexNodes.stream().map(integer -> gltf.nodes.get(integer))
        .collect(Collectors.toUnmodifiableList());
  }

  public LinkedHashSet<Integer> getIndexNodes() {
    return indexNodes;
  }
}
