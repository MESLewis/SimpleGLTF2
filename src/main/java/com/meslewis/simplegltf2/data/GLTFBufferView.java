/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
public class GLTFBufferView extends GLTFChildOfRootProperty {

  /**
   * The index of the buffer.
   */
  @JsonProperty("buffer")
  private Integer indexBuffer;

  /**
   * The offset into the buffer in bytes.
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private Integer byteOffset = 0;

  /**
   * The total byte length of the buffer view.
   */
  @JsonProperty("byteLength")
  @Min(1)
  private Integer byteLength;
  /**
   * The target that the GPU buffer should be bound to. TODO "runtime must use it to determine data
   * usage, TODO otherwise it could be inferred from mesh accessor objects.
   */
  @JsonProperty("target")
  private GLTFBufferViewTarget bufferViewTarget;
  /**
   * The stride, in bytes, between vertex attributes. When this is not defined, data is tightly
   * packed. When two or more accessors use the same bufferView, this field must be defined.
   * <p>
   * TODO multipleOf 4
   */
  @JsonProperty("byteStride")
  @Min(4)
  @Max(252)
  private Integer byteStride;

  /**
   * Converts json integer to Target enum
   */
  @JsonSetter("target")
  private void setBufferViewTarget(int value) {
    this.bufferViewTarget = GLTFBufferViewTarget.getTarget(value);
  }

  public Integer getIndexBuffer() {
    return indexBuffer;
  }

  public Integer getByteOffset() {
    return byteOffset;
  }

  public Integer getByteLength() {
    return byteLength;
  }
}
