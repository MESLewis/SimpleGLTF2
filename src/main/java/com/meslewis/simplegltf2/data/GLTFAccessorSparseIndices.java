/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GLTFAccessorSparseIndices extends GLTFProperty {

  /**
   * The index of the bufferView with sparse indices. Referenced bufferView can't have ARRAY_BUFFER
   * or ELEMENT_ARRAY_BUFFER target.
   */
  @JsonProperty("bufferView")
  @NotNull
  private Integer indexBufferView = null;

  /**
   * The offset relative to the start of the bufferView in bytes. Must be aligned.
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private Integer byteOffset = 0;

  @JsonProperty("componentType")
  @NotNull
  private GLTFAccessorSubDataType componentType = null;

  @JsonSetter("componentType")
  void setComponentType(int value) {
    GLTFAccessorSubDataType ct = GLTFAccessorSubDataType.getType(value);
    assert (ct == GLTFAccessorSubDataType.UNSIGNED_BYTE
        || ct == GLTFAccessorSubDataType.UNSIGNED_INT
        || ct == GLTFAccessorSubDataType.UNSIGNED_SHORT);
    this.componentType = GLTFAccessorSubDataType.getType(value);
  }

  public Integer getIndexBufferView() {
    return indexBufferView;
  }

  public Integer getByteOffset() {
    return byteOffset;
  }

  public GLTFAccessorSubDataType getComponentType() {
    return componentType;
  }
}
