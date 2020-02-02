/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GLTFAccessorSparse extends GLTFProperty {

  /**
   * Number of entries stored in the sparse array.
   */
  @JsonProperty("count")
  @Min(1)
  @NotNull
  private int count = -1;

  /**
   * Index array of size `count` that points to those accessor attributes that deviate from their
   * initialization value. Indices must strictly increase.
   */
  @JsonProperty("indices")
  @NotNull
  private GLTFAccessorSparseIndices indices;

  /**
   * Array of size `count` times number of components, storing the displaced accessor attributes
   * pointed by `indices`. Substituted values must have the same `componentType` and number of
   * components as the base accessor.
   * <p>
   * required
   */
  @JsonProperty("values")
  @NotNull
  private GLTFAccessorSparseValues values;

  public int getCount() {
    return count;
  }

  public GLTFAccessorSparseIndices getIndices() {
    return indices;
  }

  public GLTFAccessorSparseValues getValues() {
    return values;
  }
}
