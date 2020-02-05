/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
  private GLTFCamera camera;
  /**
   * The indices of this node's children. minItems 1
   */
  private Set<GLTFNode> children;
  /**
   * The index of the skin referenced by this node. When a skin is referenced by a node within a
   * scene, all joints used by the skin must belong to the same scene.
   */
  private GLTFSkin skin;
  /**
   * A floating-point 4x4 transformation matrix stored in column-major order. minItems 16 maxItems
   * 16
   */
  @JsonProperty("matrix")
  private Matrix4f matrix;
  /**
   * The index of the mesh in this node.
   */
  private GLTFMesh mesh;
  /**
   * The node's unit quaternion rotation in the order {x, y, z, w}, where w is the scalar. maxItems
   * 4 minItems 4
   */
  @JsonProperty("rotation")
  private final Quaternionf rotation = new Quaternionf().set(0, 0, 0, 1.0f);
  /**
   * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes. minItems
   * 3 maxItems 3
   */
  @JsonProperty("scale")
  private final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
  /**
   * The node's translation along the x, y, and z aces. minItems 3 maxItems 3
   */
  @JsonProperty("translation")
  private final Vector3f translation = new Vector3f(0.0f, 0.0f, 0.0f);
  /**
   * The weights of the instantiated Morph Target. Number of elements must match number of Morph
   * Targets of used mesh. minItems 1
   */
  @JsonProperty("weights")
  private List<Float> weights;

  public Optional<Set<GLTFNode>> getChildren() {
    return Optional.ofNullable(children);
  }

  @JsonProperty("children")
  private void setChildren(Set<Integer> indexSet) {
    gltf.indexResolvers.add(() -> {
      children = new HashSet<>();
      indexSet.forEach(index -> children.add(gltf.getNode(index)));
    });
  }

  void addSelfAndAllDescendants(List<GLTFNode> nodeList) {
    nodeList.add(this);
    getChildren().ifPresent(children -> children.stream()
        .filter(gltfNode -> !nodeList.contains(gltfNode))
        .forEach(gltfNode -> gltfNode.addSelfAndAllDescendants(nodeList)));
  }

  public Optional<GLTFSkin> getSkin() {
    return Optional.ofNullable(skin);
  }

  @JsonProperty("skin")
  private void setSkin(int index) {
    gltf.indexResolvers.add(() -> skin = gltf.getSkin(index));
  }

  public Optional<GLTFCamera> getCamera() {
    return Optional.ofNullable(camera);
  }

  @JsonProperty("camera")
  private void setCamera(int index) {
    gltf.indexResolvers.add(() -> camera = gltf.getCamera(index));
  }

  public Optional<GLTFMesh> getMesh() {
    return Optional.ofNullable(mesh);
  }

  @JsonProperty("mesh")
  private void setMesh(int index) {
    gltf.indexResolvers.add(() -> mesh = gltf.getMesh(index));
  }

  public Matrix4f getMatrix() {
    return matrix;
  }

  public Quaternionf getRotation() {
    return rotation;
  }

  public Vector3f getScale() {
    return scale;
  }

  public Vector3f getTranslation() {
    return translation;
  }

  public List<Float> getWeights() {
    return weights;
  }
}
