/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import javax.validation.constraints.NotNull;

/**
 * A set of primitives to be rendered.  A node can contain one mesh.  A node's transform places the
 * mesh in the scene.
 */
public class GLTFMesh extends GLTFChildOfRootProperty {

  /**
   * An array of primitives, each defining geometry to be rendered with a material. minItems 1
   */
  @JsonProperty("primitives")
  @NotNull
  private ArrayList<GLTFMeshPrimitive> primitives;

  /**
   * Array of weights to be applied to the Morph Targets. minItems 1
   */
  @JsonProperty("weights")
  private ArrayList<Integer> weights;

  public ArrayList<GLTFMeshPrimitive> getPrimitives() {
    return primitives;
  }

  public ArrayList<Integer> getWeights() {
    return weights;
  }
}
