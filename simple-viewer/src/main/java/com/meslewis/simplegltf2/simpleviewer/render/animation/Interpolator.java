/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render.animation;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFAnimationChannelTarget.GLTFPath;
import com.meslewis.simplegltf2.data.GLTFAnimationSampler;
import com.meslewis.simplegltf2.data.GLTFChannel;
import com.meslewis.simplegltf2.simpleviewer.render.RenderNode;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interpolator {

  private static final Logger logger = LoggerFactory.getLogger(Interpolator.class);

  private int prevKey = 0;
  private float prevT = 0.0f;
  private final GLTFChannel channel;
  private RenderNode renderNode;

  private final Quaternionf endQ = new Quaternionf();

  public Interpolator(GLTFChannel channel) {
    this.channel = channel;
    if (channel.getTarget().getNode().isPresent()) {
      var node = channel.getTarget().getNode().get();
      renderNode = RenderNode.from(node).orElseThrow();
    }
  }

  private void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Quaternionf quantDest, Vector3f vectorDest, float[] floatArrayDest) {
    GLTFAccessor input = sampler.getInput();
    GLTFAccessor output = sampler.getOutput();

    //Translate time to key frame

    //No interpolation for single key frame animations
    if (output.getElementCount() == 1) {
      if (vectorDest != null) {
        readInto(output, 0, vectorDest);
      } else if (quantDest != null) {
        readInto(output, 0, quantDest);
      } else {
        logger.error("Unhandled single key frame");
      }
      return;
    }

    int primCount = input.getPrimitiveCount();
    float maxKeyTime = input.getFloat(primCount - 1);
    totalTime %= maxKeyTime;

    if (this.prevT > totalTime) {
      this.prevKey = 0;
    }

    this.prevT = totalTime;

    //Find next keyrame: min{ t of input | t > prevKey}
    int nextKey = -1;
    for (int i = this.prevKey; i < input.getPrimitiveCount(); i++) {
      if (totalTime <= input.getFloat(i)) {
        nextKey = i;
        nextKey = Math.max(nextKey, 1);
        nextKey = Math.min(nextKey, input.getPrimitiveCount());
        break;
      }
    }

    this.prevKey = nextKey - 1;
    this.prevKey = Math.min(nextKey, this.prevKey);
    this.prevKey = Math.max(0, this.prevKey);

    float keyDelta = input.getFloat(nextKey) - input.getFloat(prevKey);

    //Normalize t: [t0, t1] -> [0, 1]
    float tn = (totalTime - input.getFloat(prevKey)) / keyDelta;

//    logger.debug(
//        "PrevKey: " + prevKey + " NextKey: " + nextKey + " TN:" + tn + " KeyDelta: " + keyDelta);

    //Set values based on channel target path
    if (channel.getTarget().getPath() == GLTFPath.ROTATION) {
      switch (sampler.getInterpolation()) {
        case CUBICSPLINE:
          //GLTF requires cubic spline interpolation for quaternions
          float[] spline = cubicSpline(prevKey, nextKey, output, keyDelta, tn, 4);
          quantDest.set(spline[0], spline[1], spline[2], spline[3]);
          quantDest.normalize();
          break;
        case LINEAR:
          readInto(output, this.prevKey, quantDest);
          readInto(output, nextKey, endQ);
          quantDest.slerp(endQ, tn);
          quantDest.normalize();
          break;
        case STEP:
          readInto(output, prevKey, quantDest);
          break;
      }
    } else if (vectorDest != null) {
      //This block only handles Vector3f for translation and scale
      Vector3f endV = new Vector3f();

      readInto(output, prevKey, vectorDest);
      readInto(output, nextKey, endV);

      switch (sampler.getInterpolation()) {
        case STEP:
          readInto(output, prevKey, vectorDest);
          break;
        case CUBICSPLINE:
          float[] spline = cubicSpline(prevKey, nextKey, output, keyDelta, tn, 3);
          vectorDest.set(spline[0], spline[1], spline[2]);
          break;
        case LINEAR:
          vectorDest.lerp(endV, tn, vectorDest);
          break;
        default:
          logger.error("Not implemented");
      }
    } else if (floatArrayDest != null) {
      switch (sampler.getInterpolation()) {
        case STEP:
          readInto(output, prevKey, floatArrayDest);
          break;
        case CUBICSPLINE:
          float[] spline = cubicSpline(prevKey, nextKey, output, keyDelta, tn, 3);
          for (int i = 0; i < floatArrayDest.length; i++) {
            floatArrayDest[i] = spline[i];
          }
          break;
        case LINEAR:
          linear(prevKey, nextKey, output, tn, floatArrayDest.length, floatArrayDest);
          break;
        default:
          logger.error("Not implemented");
      }
    }
  }

  public void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Vector3f dest) {
    interpolate(totalTime, sampler, null, dest, null);
  }

  public void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Quaternionf dest) {
    interpolate(totalTime, sampler, dest, null, null);
  }

  public void interpolate(float totalTime, GLTFAnimationSampler sampler,
      float[] dest) {
    interpolate(totalTime, sampler, null, null, dest);
  }

  private float[] cubicSpline(int prevKey, int nextKey, GLTFAccessor output,
      float keyDelta, float t, int stride) {

    //Scale by 3, because each output entry consists of two tangents and one data point.
    int prevIndex = prevKey * stride * 3;
    int nextIndex = nextKey * stride * 3;

    int A = 0;
    int V = 1 * stride;
    int B = 2 * stride;

    float tSq = t * t;
    float tCub = t * t * t;

    // We assume that the components in output are laid out like this: in-tangent, point, out-tangent.
    // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#appendix-c-spline-interpolation
    float[] ret = new float[stride];
    for (int i = 0; i < stride; ++i) {
      float v0 = output.getFloat(prevIndex + i + V);
      float a = keyDelta * output.getFloat(nextIndex + i + A);
      float b = keyDelta * output.getFloat(prevIndex + i + B);
      float v1 = output.getFloat(nextIndex + i + V);

      ret[i] = ((2 * tCub - 3 * tSq + 1) * v0)
          + ((tCub - 2 * tSq + t) * b)
          + ((-2 * tCub + 3 * tSq) * v1)
          + ((tCub - tSq) * a);

    }
    return ret;
  }

  private void linear(int prevKey, int nextKey, GLTFAccessor output, float tn, int stride,
      float[] dest) {
    float[] prevFloats = new float[stride];
    readInto(output, prevKey, prevFloats);
    float[] nextFloats = new float[stride];
    readInto(output, nextKey, nextFloats);
    for (int i = 0; i < stride; i++) {
      dest[i] = prevFloats[i] * (1 - tn) + nextFloats[i] * tn;
    }
  }

  /**
   * elementIndex is coming from the array of key frames. Multiply this index by the number of
   * primitives in each key frame.
   */
  private void readInto(GLTFAccessor output, int elementIndex, Quaternionf dest) {
    elementIndex *= output.getDataType().getPrimitiveCount();
    dest.set(output.getFloat(elementIndex++), output.getFloat(elementIndex++),
        output.getFloat(elementIndex++), output.getFloat(elementIndex));
  }

  private void readInto(GLTFAccessor output, int elementIndex, Vector3f dest) {
    elementIndex *= output.getDataType().getPrimitiveCount();
    dest.set(output.getFloat(elementIndex++), output.getFloat(elementIndex++),
        output.getFloat(elementIndex));
  }

  private void readInto(GLTFAccessor output, int elementIndex, float[] dest) {
    elementIndex *= output.getDataType().getPrimitiveCount() * dest.length;
    for (int i = 0; i < dest.length; i++) {
      dest[i] = output.getFloat(elementIndex++);
    }
  }

  public GLTFChannel getChannel() {
    return channel;
  }

  public RenderNode getRenderNode() {
    return this.renderNode;
  }
}
