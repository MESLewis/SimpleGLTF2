/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import com.meslewis.simplegltf2.data.GLTFNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RenderNode {

  private static final String EXTRA_KEY = "_RenderNode";
  protected AABBf boundingBox;
  private RenderSkin skin;
  private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
  private Vector3f translation = new Vector3f();
  private Quaternionf rotation = new Quaternionf();
  private final Matrix4f worldTransform = new Matrix4f();
  private final Matrix4f inverseWorldTransform = new Matrix4f();
  private final Matrix4f normalMatrix = new Matrix4f();
  private final List<RenderNode> children = new ArrayList<>();
  private boolean changed;
  private Matrix4f localTransform = null;

  public RenderNode(GLTFNode node, RenderNode parent) {

    if (node != null) {
      node.getExtras().put(EXTRA_KEY, this);
      if (node.getMatrix() != null) {
        applyMatrix(node.getMatrix());
      } else {
        Vector3fc scalef = node.getScale();
        scale = new Vector3f(scalef);

        Quaternionfc rotf = node.getRotation();
        rotation = new Quaternionf().set(rotf);

        Vector3fc traf = node.getTranslation();
        translation = new Vector3f(traf);
      }
      if (node.getSkin().isPresent()) {
        this.skin = new RenderSkin(node.getSkin().get());
      }
    }

    //Register as child
    if (parent != null) {
      parent.addChild(this);
    }
  }

  public static Optional<RenderNode> from(GLTFNode node) {
    var extras = node.getExtras();
    if (extras.containsKey(EXTRA_KEY)) {
      return Optional.ofNullable((RenderNode) extras.get(EXTRA_KEY));
    } else {
      return Optional.empty();
    }
  }

  public static Optional<RenderNode> from(Optional<GLTFNode> node) {
    if (node.isEmpty()) {
      return Optional.empty();
    } else {
      return RenderNode.from(node.get());
    }
  }

  void addChild(RenderNode child) {
    this.children.add(child);
  }

  public List<RenderNode> getChildren() {
    return this.children;
  }

  private void applyMatrix(Matrix4fc floatMatrix) {
    Matrix4f matrix = new Matrix4f().set(floatMatrix);
    localTransform = matrix;
    matrix.getScale(scale);
    matrix.getUnnormalizedRotation(rotation);
    matrix.getTranslation(translation);
    changed = true;
  }

  private Matrix4f getLocalTransform() {
    if (localTransform == null) {
      localTransform = new Matrix4f();
      changed = true;
    }
    if (changed) {
      localTransform.identity();
      localTransform.translationRotateScale(translation, rotation, scale);
      changed = false;
    }
    return this.localTransform;
  }

  public Matrix4f getWorldTransform() {
    assert (!changed);
    return this.worldTransform;
  }

  public Matrix4f getInverseWorldTransform() {
    return inverseWorldTransform;
  }

  public void applyTransform(Matrix4f parentTransform) {
    Matrix4f localTransform = getLocalTransform();
    parentTransform.mul(localTransform, worldTransform);
    worldTransform.invert(inverseWorldTransform);
    inverseWorldTransform.transpose(normalMatrix);

    for (RenderNode child : children) {
      child.applyTransform(this.getWorldTransform());
    }
  }

  public Matrix4f getNormalMatrix() {
    return normalMatrix;
  }

  /**
   * Get the axis aligned bounding box for this node
   *
   * @return
   */
  public AABBf getBoundingBox() {
    if (boundingBox == null) {
      boundingBox = new AABBf();
    }
    return boundingBox;
  }

  public Vector3f getTranslation() {
    this.changed = true;
    return this.translation;
  }

  public Vector3f getScale() {
    this.changed = true;
    return this.scale;
  }

  public Quaternionf getRotation() {
    this.changed = true;
    return this.rotation;
  }

  public Optional<RenderSkin> getSkin() {
    return Optional.ofNullable(skin);
  }

  public void updateSkin() {
    for (RenderNode child : children) {
      child.updateSkin();
    }
  }
}
