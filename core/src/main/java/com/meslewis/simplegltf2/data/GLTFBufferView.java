/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
public class GLTFBufferView extends GLTFChildOfRootProperty {

  /**
   * The index of the buffer.
   */
  private GLTFBuffer buffer;

  @JsonProperty("buffer")
  private void setBuffer(int index) {
    gltf.indexResolvers.add(() -> buffer = gltf.getBuffer(index));
  }

  /**
   * The offset into the buffer in bytes.
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private int byteOffset = 0;

  /**
   * The total byte length of the buffer view.
   */
  @JsonProperty("byteLength")
  @Min(1)
  private int byteLength = -1;
  /**
   * The target that the GPU buffer should be bound to. TODO "runtime must use it to determine data
   * usage, TODO otherwise it could be inferred from mesh accessor objects.
   */
  @JsonProperty("target")
  @Valid
  private GLTFBufferViewTarget bufferViewTarget;
  /**
   * The stride, in bytes, between vertex attributes. When this is not defined, data is tightly
   * packed. When two or more accessors use the same bufferView, this field must be defined.
   * <p>
   * TODO multipleOf 4
   */
  @JsonProperty("byteStride")
  @Min(0)
  @Max(252)
  private int byteStride = 0;

  /**
   * Converts json integer to Target enum
   */
  @JsonSetter("target")
  private void setBufferViewTarget(int value) {
    this.bufferViewTarget = GLTFBufferViewTarget.getTarget(value);
  }

  public GLTFBufferViewTarget getTarget() {
    return this.bufferViewTarget;
  }

  public int getByteOffset() {
    return byteOffset;
  }

  public int getByteLength() {
    return byteLength;
  }

  /**
   * @return
   */
  GLTFBuffer getDataBuffer() {
    return buffer;
  }

  /**
   * @return Buffer filled with data this BufferView points to
   */
  ByteBuffer getData(int byteOffset, int byteLength) {
    if (byteOffset + byteLength > this.byteLength) {
      throw new BufferUnderflowException();
    }
    return getDataBuffer().getData(this.byteOffset + byteOffset, byteLength);
  }

  /**
   * @return
   */
  int getByteStride() {
    return this.byteStride;
  }
}
