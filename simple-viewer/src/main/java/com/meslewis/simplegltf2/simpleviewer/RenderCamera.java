/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.util.List;
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
  static float Z_NEAR = 1f;
  static float Z_FAR = 1000f;

  private float aspectRatio = ((float) RenderCamera.WIDTH) / RenderCamera.HEIGHT;

  private Vector3f position = new Vector3f(-10, 0, 0);
  private Vector3f target = new Vector3f();
  private Vector3f up = new Vector3f(0, 1, 0);
  private AxisAngle4f rotation = new AxisAngle4f();
  private float zoom;

  public RenderCamera() {
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

  public void fitViewToScene(List<RenderObject> renderObjects) {
    AABBf sceneBounds = new AABBf();
    getSceneExtends(renderObjects, sceneBounds);

    fitCameraTargetToExtends(sceneBounds);
    fitZoomToExtends(sceneBounds);
  }

  public float getAspectRatio() {
    return aspectRatio;
  }

  private void getSceneExtends(List<RenderObject> objects, AABBf bounds) {
    for (RenderObject ro : objects) {
      bounds.union(ro.getBoundingBox());
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
