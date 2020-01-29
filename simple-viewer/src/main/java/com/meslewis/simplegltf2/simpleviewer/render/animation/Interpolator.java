/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render.animation;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFAnimationSampler;
import com.meslewis.simplegltf2.data.GLTFChannel;
import com.meslewis.simplegltf2.data.GLTFInterpolation;
import com.meslewis.simplegltf2.data.GLTFPath;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interpolator {

  private static final Logger logger = LoggerFactory.getLogger(Interpolator.class);

  private int prevKey = 0;
  private float prevT = 0.0f;
  private GLTFChannel channel;

  private Quaternionf endQ = new Quaternionf();

  public Interpolator(GLTFChannel channel) {
    this.channel = channel;
  }

  private void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Quaternionf quantDest, Vector3f vectorDest) {
    GLTFAccessor input = sampler.getInput().orElseThrow();
    GLTFAccessor output = sampler.getOutput().orElseThrow();

    //Translate time to key frame

    //No interpolation for single key frame animations
    if (output.getElementCount() == 1) {
      readInto(output, 0, vectorDest);//TODO handle Quant
      return;
    }

//    totalTime = Math.max(totalTime % input.getFloat(input.getElementCount() - 1), input.getFloat(0));
    int primCount = input.getPrimitiveCount();
    float maxKeyTime = input.getFloat(primCount - 1);
    totalTime = totalTime % maxKeyTime;

    if (this.prevT > totalTime) {
      this.prevKey = 0;
    }

    this.prevT = totalTime;

    //Find next keyrame: min{ t of input | t > prevKey}
    int nextKey = -1;
    for (int i = this.prevKey; i < input.getPrimitiveCount(); i++) {
      if (totalTime <= input.getFloat(i)) {
        i = Math.max(i, 1);
        i = Math.min(i, input.getPrimitiveCount());
        nextKey = i;
        break;
      }
    }

    this.prevKey = Math.max(0, Math.min(nextKey - 1, nextKey));

    float keyDelta = input.getFloat(nextKey) - input.getFloat(prevKey);

    //Normalize t: [t0, t1] -> [0, 1]
    float tn = (totalTime - input.getFloat(prevKey)) / keyDelta;

    assert (tn >= 0);

    //Set values based on channel target path
    if (channel.getTarget().getPath() == GLTFPath.ROTATION) {
      if (sampler.getInterpolation() == GLTFInterpolation.CUBICSPLINE) {
        //GLTF requires cubic spline interpolation for quaternions
        cubicSpline(prevKey, nextKey, output, quantDest, keyDelta, tn);
        quantDest.normalize();
      } else {
        readInto(output, this.prevKey, quantDest);
        quantDest.normalize();
        readInto(output, nextKey, endQ);
        endQ.normalize();
        quantDest.slerp(endQ, tn);
        quantDest.normalize();
      }
    } else {
      //This block only handles Vector3f for translation and scale
      Vector3f beginV = new Vector3f(); //TODO use destination
      Vector3f endV = new Vector3f();

      readInto(output, prevKey, beginV);
      readInto(output, nextKey, endV);

      switch (sampler.getInterpolation()) {
        case STEP:
          readInto(output, prevKey, vectorDest);
          break;
        case CUBICSPLINE:
          logger.error("Not implemented.");
          break;
        case LINEAR:
          beginV.lerp(endV, tn, vectorDest);
          break;
        default:
          logger.error("Not implemented");
      }
    }
  }

  public void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Vector3f dest) {
    interpolate(totalTime, sampler, null, dest);
  }

  public void interpolate(float totalTime, GLTFAnimationSampler sampler,
      Quaternionf dest) {
    interpolate(totalTime, sampler, dest, null);
  }

  private void cubicSpline(int prevKey, int nextKey, GLTFAccessor output, Quaternionf dest,
      float keyDelta, float t) {
    final int stride = 4; //Number of components in a quaternion

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
    for (int i = 0; i < stride; ++i) {
      float v0 = output.getFloat(prevIndex + i + V);
      float a = keyDelta * output.getFloat(nextIndex + i + A);
      float b = keyDelta * output.getFloat(prevIndex + i + B);
      float v1 = output.getFloat(nextIndex + i + V);

      float result = ((2 * tCub - 3 * tSq + 1) * v0)
          + ((tCub - 2 * tSq + t) * b)
          + ((-2 * tCub + 3 * tSq) * v1)
          + ((tCub - tSq) * a);

      switch (i) {
        case 0:
          dest.x = result;
          break;
        case 1:
          dest.y = result;
          break;
        case 2:
          dest.z = result;
          break;
        case 3:
          dest.w = result;
          break;
      }
    }
  }

  /**
   * elementIndex is coming from the array of key frames. Multiply this index by the number of
   * primitives in each key frame.
   */
  private void readInto(GLTFAccessor output, int elementIndex, Quaternionf dest) {
    elementIndex = elementIndex * output.getDataType().getPrimitiveCount();
    dest.set(output.getFloat(elementIndex++), output.getFloat(elementIndex++),
        output.getFloat(elementIndex++), output.getFloat(elementIndex));
  }

  private void readInto(GLTFAccessor output, int elementIndex, Vector3f dest) {
    elementIndex = elementIndex * output.getDataType().getPrimitiveCount();
    dest.set(output.getFloat(elementIndex++), output.getFloat(elementIndex++),
        output.getFloat(elementIndex));
  }

}
