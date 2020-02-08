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
  @NotNull
  private GLTFBufferView bufferView;
  /**
   * The offset relative to the start of the bufferView in bytes. Must be aligned.
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private int byteOffset = 0;
  @JsonProperty("componentType")
  @NotNull
  private GLTFAccessorComponentType componentType = null;

  @JsonProperty("bufferView")
  private void setBufferView(int index) {
    gltf.indexResolvers.add(() -> bufferView = gltf.getBufferView(index));
  }

  public int getByteOffset() {
    return byteOffset;
  }

  public GLTFAccessorComponentType getComponentType() {
    return componentType;
  }

  @JsonSetter("componentType")
  private void setComponentType(int value) {
    GLTFAccessorComponentType ct = GLTFAccessorComponentType.getType(value);
    assert (ct == GLTFAccessorComponentType.UNSIGNED_BYTE
        || ct == GLTFAccessorComponentType.UNSIGNED_INT
        || ct == GLTFAccessorComponentType.UNSIGNED_SHORT);
    this.componentType = GLTFAccessorComponentType.getType(value);
  }
}
