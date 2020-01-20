/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

/**
 * Accessor type
 */
public enum GLTFAccessorDataType {
  SCALAR(1),
  VEC2(2),
  VEC3(3),
  VEC4(4),
  MAT2(4),
  MAT3(9),
  MAT4(16);

  /**
   * Number of sub elements for this data type
   */
  private final Integer primitiveCount;

  GLTFAccessorDataType(int elementCount) {
    this.primitiveCount = elementCount;
  }

  public Integer getPrimitiveCount() {
    return primitiveCount;
  }
}
