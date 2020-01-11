/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Array of size `accessor.sparse.count` times number of components storing the displaced accessor
 * attributes pointed by `accessor.sparse.indices`.
 */
public class GLTFAccessorSparseValues extends GLTFProperty {

  /**
   * The index of the bufferView with sparse values. Referenced bufferView can't have ARRAY_BUFFER
   * or ELEMENT_ARRAY_BUFFER target.
   * <p>
   * required
   */
  @JsonProperty("bufferView")
  @Min(0)
  @NotNull
  private Integer bufferView;

  /**
   * The offset relative to the start of the bufferView in bytes. Must be aligned.
   * <p>
   * default 0
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private Integer byteOffset = 0;

  public Integer getBufferView() {
    return bufferView;
  }

  public Integer getByteOffset() {
    return byteOffset;
  }
}
