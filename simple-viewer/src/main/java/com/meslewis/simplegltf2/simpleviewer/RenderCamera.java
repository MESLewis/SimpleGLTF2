/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import org.joml.AABBf;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Only used for user camera
public class RenderCamera {

  private static final Logger logger = LoggerFactory.getLogger(RenderCamera.class);
  static final int WIDTH = 700;
  static final int HEIGHT = 500;
  static float FOVY = 70f;
  static float Z_NEAR = 0.0001f;
  static float Z_FAR = 100000f;
  private float zoomFactor = 1.04f;
  private float rotateSpeed = (float) 1 / 180;

  private float aspectRatio = ((float) RenderCamera.WIDTH) / RenderCamera.HEIGHT;

  private Vector3f position = new Vector3f(0, 0, 0);
  private Vector3f target = new Vector3f();
  private Vector3f up = new Vector3f(0, 1, 0);
  private AxisAngle4f rotation = new AxisAngle4f();
  private float zoom;

  public RenderCamera() {
  }

  public void zoom(float direction) {
    if (direction < 0) {
      this.zoom *= this.zoomFactor;
    } else {
      this.zoom /= this.zoomFactor;
    }
  }

  public void rotate(float x, float y) {
    this.rotation.x += (x * this.rotateSpeed);
    this.rotation.y += (y * this.rotateSpeed);
    float yMax = (float) (Math.PI / 2 - 0.01);
    this.rotation.y = Math.min(this.rotation.y, yMax);
    this.rotation.y = Math.max(this.rotation.y, -yMax);
  }

  public void updatePosition() {
    //Calculate direction from focus to camera (assuming camera is at positive z)
    Vector3f direction = new Vector3f(0, 0, 1);
    toLocalRotation(direction);

    position.zero();
    position.add(direction.mul(zoom));
    position.add(target);
  }

  private void toLocalRotation(Vector3f direction) {
    //Rotate x by y and y by x. Not sure why.
    direction.rotateX(-rotation.y);
    direction.rotateY(-rotation.x);
  }

  public Matrix4f getProjectionMatrix() {
    Matrix4f projection = new Matrix4f().identity();

    projection.perspective(FOVY, aspectRatio, Z_NEAR, Z_FAR);

    return projection;
  }

  public Matrix4f getViewMatrix() {
    Matrix4f view = new Matrix4f().identity();
    view.setLookAt(getPosition(), getLookAtTarget(), up);
    return view;
  }

  public Vector3f getLookAtTarget() {
    return target;
  }

  public Vector3f getPosition() {
    return this.position;
  }

  public void fitViewToScene(RenderNode rootNode) {
    AABBf sceneBounds = new AABBf();
    getSceneExtends(rootNode, sceneBounds);

    fitCameraTargetToExtends(sceneBounds);
    fitZoomToExtends(sceneBounds);
  }

  public float getAspectRatio() {
    return aspectRatio;
  }

  private void getSceneExtends(RenderNode rootNode, AABBf bounds) {
    for (RenderNode rn : rootNode.getChildren()) {
      bounds.union(rn.getBoundingBox());
      getSceneExtends(rn, bounds);
    }
  }

  private void fitCameraTargetToExtends(AABBf bounds) {
    for (int i = 0; i < 3; i++) {
      float mid = (bounds.getMax(i) + bounds.getMin(i)) / 2;
      this.target.setComponent(i, mid);
    }
  }

  private void fitZoomToExtends(AABBf bounds) {
    float maxAxisLength = Math.max(bounds.maxX - bounds.minX, bounds.maxY - bounds.minY);

    float yfov = FOVY;
    float xfov = FOVY * this.aspectRatio;

    double yZoom = maxAxisLength / 2 / Math.tan(yfov / 2);
    double xZoom = maxAxisLength / 2 / Math.tan(xfov / 2);

    this.zoom = (float) Math.max(xZoom, yZoom);
  }
}
