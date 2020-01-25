/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A typed view into a bufferView. A bufferView contains raw binary data. An accessor provides a
 * typed view into a bufferView or a subset of a bufferView similar to how WebGL
 */
public class GLTFAccessor extends GLTFChildOfRootProperty {

  private static final Logger logger = LoggerFactory.getLogger(GLTFAccessor.class);

  /**
   * The data type of components in the attribute. All valid values correspond to WebGL enums. The
   * corresponding typed arrays are `Int8Array`, `Uint8Array`, `Int16Array`, `Uint16Array`,
   * `Uint32Array`, and `Float32Array`, respectively.  5125 (UNSIGNED_INT) is only allowed when the
   * accessor contains indices, i.e., the accessor is only referenced by `primitive.indices`.
   */
  @JsonProperty("componentType")
  private GLTFAccessorPrimitiveType subDataType;
  /**
   * Specifies whether integer data values should be normalized (`true`) to [0, 1] (for unsigned
   * types) or [-1, 1] (for signed types), or converted directly (`false`) when they are accessed.
   * This property is defined only for accessors that contain vertex attributes or animation output
   * data.
   */
  @JsonProperty("normalized")
  private boolean normalized = false;
  /**
   * Specifies if the attribute is a scalar, vector, or matrix.
   */
  @JsonProperty("type")
  private GLTFAccessorDataType dataType;
  /**
   * Maximum value of each component in this attribute.  Array elements must be treated as having
   * the same data type as accessor's `componentType`. Both min and max arrays have the same length.
   * The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.
   * <p>
   * `normalized` property has no effect on array values: they always correspond to the actual
   * values stored in the buffer. When accessor is sparse, this property must contain max values of
   * accessor data with sparse substitution applied.
   * <p>
   * //min items 1 max items 16
   */
  @JsonProperty("max")
  private ArrayList<Float> max;
  /**
   * Minimum value of each component in this attribute.  Array elements must be treated as having
   * the same data type as accessor's `componentType`. Both min and max arrays have the same length.
   * The length is determined by the value of the type property; it can be 1, 2, 3, 4, 9, or 16.
   * <p>
   * `normalized` property has no effect on array values: they always correspond to the actual
   * values stored in the buffer. When accessor is sparse, this property must contain min values of
   * accessor data with sparse substitution applied.
   * <p>
   * //min items 1 max items 16
   */
  @JsonProperty("min")
  private ArrayList<Float> min;
  /**
   * The index of the bufferView. When not defined, accessor must be initialized with zeros;
   * `sparse` property or extensions could override zeros with actual values. TODO when not defined
   */
  @JsonProperty("bufferView")
  @Min(0)
  private Integer indexBufferView;
  /**
   * The offset relative to the start of the bufferView in bytes. This must be a multiple of the
   * size of the component data type.
   */
  @JsonProperty("byteOffset")
  @Min(0)
  private Integer byteOffset = 0;
  /**
   * The number of attributes referenced by this accessor, not to be confused with the number of
   * bytes or number of components.
   */
  @JsonProperty("count")
  @Min(1)
  private Integer elementCount;
  /**
   *
   */
  @JsonProperty("sparse")
  private GLTFAccessorSparse sparse;

  @JsonSetter("componentType")
  private void setSubDataType(int value) {
    this.subDataType = GLTFAccessorPrimitiveType.getType(value);
  }

  /**
   * Get referenced BufferView
   *
   * @return
   */
  private GLTFBufferView getBufferView() {
    return gltf.getBufferView(indexBufferView);
  }

  /**
   * 3 layers of length to calculate the length in bytes if byteStride is defined it includes the
   * element size
   *
   * @return the size of the entire Accessor in bytes
   */
  public int getSizeInBytes() {
    int elementSizeInBytes =
        getDataType().getPrimitiveCount() * getPrimitiveType().getSizeInBytes();
    int byteStride = getByteStride() - elementSizeInBytes;
    if (getByteStride() > 0) {
      return ((elementCount - 1) * byteStride) + (elementCount * elementSizeInBytes);
    }
    return elementCount * elementSizeInBytes;
  }

  /**
   * @return a Buffer containing data this Accessor references //TODO sparse
   */
  public ByteBuffer getData() {
    try {
      return this.getBufferView().getData(byteOffset, getSizeInBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    logger.error("BufferView data read failed");
    return ByteBuffer.allocateDirect(0);
  }

  public GLTFBufferViewTarget getTarget() {
    return this.getBufferView().getTarget();
  }

  /**
   * The stride is how many bytes should be ignored between each value read from the buffer
   *
   * @return the stride in bytes.
   */
  public int getByteStride() {
    return this.getBufferView().getByteStride();
  }

  public int getElementCount() {
    return this.elementCount;
  }

  public int getPrimitiveCount() {
    return this.elementCount * this.getDataType().getPrimitiveCount();
  }

  public int getGLType() {
    return this.subDataType.getValue();
  }

  public boolean isNormalized() {
    return normalized;
  }

  public GLTFAccessorPrimitiveType getPrimitiveType() {
    return subDataType;
  }

  public GLTFAccessorDataType getDataType() {
    return dataType;
  }

  public ArrayList<Float> getMax() {
    return max;
  }

  public ArrayList<Float> getMin() {
    return min;
  }

  public Integer getByteOffset() {
    return byteOffset;
  }

  public GLTFAccessorSparse getSparse() {
    return sparse;
  }
}
