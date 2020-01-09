/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * A node in the node hierarchy.  When the node contains `skin`, all `mesh.primitives` must contain
 * `JOINTS_0` and `WEIGHTS_0` attributes.  A node can have either a `matrix` or any combination of
 * `translation`/`rotation`/`scale` (TRS) properties. TRS properties are converted to matrices and
 * postmultiplied in the `T * R * S` order to compose the transformation matrix; first the scale is
 * applied to the vertices, then the rotation, and then the translation. If none are provided, the
 * transform is the identity. When a node is targeted for animation (referenced by an
 * animation.channel.target), only TRS properties may be present; `matrix` will not be present.
 */
public class GLTFNode extends GLTFChildOfRootProperty {

  /**
   * The index of the camera referenced by this node.
   */
  @JsonProperty("camera")
  private Integer indexCamera;

  /**
   * The indices of this node's children. minItems 1
   */
  @JsonProperty("children")
  private LinkedHashSet<Integer> indexChildren;

  /**
   * The index of the skin referenced by this node. When a skin is referenced by a node within a
   * scene, all joints used by the skin must belong to the same scene.
   */
  @JsonProperty("skin")
  private Integer indexSkin;

  /**
   * A floating-point 4x4 transformation matrix stored in column-major order. minItems 16 TODO
   * maxItems 16
   */
  @JsonProperty("matrix")
  private Float[] matrix = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

  /**
   * The index of the mesh in this node.
   */
  @JsonProperty("mesh")
  private Integer indexMesh;

  /**
   * The node's unit quaternion rotation in the order {x, y, z, w}, where w is the scalar. maxItems
   * 4 minItems 4
   */
  @JsonProperty("rotation")
  private Float[] rotation = {0.0f, 0.0f, 0.0f, 1.0f};

  /**
   * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes. minItems
   * 3 maxItems 3
   */
  @JsonProperty("scale")
  private Float[] scale = {1.0f, 1.0f, 1.0f};

  /**
   * The node's translation along the x, y, and z aces. minItems 3 maxItems 3
   */
  @JsonProperty("translation")
  private Float[] translation = {0.0f, 0.0f, 0.0f};

  /**
   * The weights of the instantiated Morph Target. Number of elements must match number of Morph
   * Targets of used mesh. minItems 1
   */
  @JsonProperty("weights")
  private ArrayList<Float> weights;

  public GLTFCamera getCamera() {
    return gltf.getCamera(indexCamera);
  }

  public LinkedHashSet<GLTFNode> getChildren() {
    return indexChildren.stream().map(integer -> gltf.getNode(integer)).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public GLTFSkin getSkin() {
    return gltf.getSkin(indexSkin);
  }

  public GLTFMesh getMesh() {
    return gltf.getMesh(indexMesh);
  }

  public Float[] getMatrix() {
    return matrix;
  }

  public Float[] getRotation() {
    return rotation;
  }

  public Float[] getScale() {
    return scale;
  }

  public Float[] getTranslation() {
    return translation;
  }

  public ArrayList<Float> getWeights() {
    return weights;
  }

  public Integer getIndexCamera() {
    return indexCamera;
  }

  public LinkedHashSet<Integer> getIndexChildren() {
    return indexChildren;
  }

  public Integer getIndexSkin() {
    return indexSkin;
  }

  public Integer getIndexMesh() {
    return indexMesh;
  }
}
