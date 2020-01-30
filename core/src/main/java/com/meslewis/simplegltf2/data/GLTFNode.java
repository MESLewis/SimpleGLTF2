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
import java.util.Objects;
import java.util.Optional;
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
  private static final LinkedHashSet EMPTY_LINKED_HASH_SET = new LinkedHashSet();

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
  private float[] matrix;// = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

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
  private float[] rotation = {0.0f, 0.0f, 0.0f, 1.0f};

  /**
   * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes. minItems
   * 3 maxItems 3
   */
  @JsonProperty("scale")
  private float[] scale = {1.0f, 1.0f, 1.0f};

  /**
   * The node's translation along the x, y, and z aces. minItems 3 maxItems 3
   */
  @JsonProperty("translation")
  private float[] translation = {0.0f, 0.0f, 0.0f};

  /**
   * The weights of the instantiated Morph Target. Number of elements must match number of Morph
   * Targets of used mesh. minItems 1
   */
  @JsonProperty("weights")
  private ArrayList<Float> weights;

  private LinkedHashSet<GLTFNode> children;

  public LinkedHashSet<GLTFNode> getChildren() {
    if (children == null) {
      if (indexChildren == null) {
        children = GLTFNode.EMPTY_LINKED_HASH_SET;
      } else {
        children = indexChildren.stream().filter(Objects::nonNull)
            .filter(integer -> gltf.getNode(integer).isPresent())
            .map(integer -> gltf.getNode(integer).get())
            .collect(Collectors.toCollection(LinkedHashSet::new));
      }
    }
    return children;
  }

  void addSelfAndAllDescendants(List<GLTFNode> nodeList) {
    nodeList.add(this);
    getChildren().stream()
        .filter(gltfNode -> !nodeList.contains(gltfNode))
        .forEach(gltfNode -> gltfNode.addSelfAndAllDescendants(nodeList));
  }

  public GLTFSkin getSkin() {
    return gltf.getSkin(indexSkin);
  }

  public Optional<GLTFCamera> getCamera() {
    return gltf.getCamera(indexCamera);
  }

  public Optional<GLTFMesh> getMesh() {
    return gltf.getMesh(indexMesh);
  }

  public float[] getMatrix() {
    return matrix;
  }

  public float[] getRotation() {
    return rotation;
  }

  public float[] getScale() {
    return scale;
  }

  public float[] getTranslation() {
    return translation;
  }

  public ArrayList<Float> getWeights() {
    return weights;
  }

  public void setRotation(float[] rotation) {
    this.rotation = rotation;
  }

  public void setScale(float[] scale) {
    this.scale = scale;
  }

  public void setTranslation(float[] translation) {
    this.translation = translation;
  }
}
