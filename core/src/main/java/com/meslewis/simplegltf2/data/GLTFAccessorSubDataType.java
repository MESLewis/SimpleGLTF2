/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Each entry implements Integer / float conversion according to the GLTF spec
 */
public enum GLTFAccessorSubDataType {
  BYTE(5120, 1) {
    @Override
    public Float intToFloat(int input) {
      return Math.max(input / 127.0f, -1.0f);
    }

    @Override
    public Integer floatToInt(Float input) {
      return Math.round(input * 127.0f);
    }
  },
  UNSIGNED_BYTE(5121, 1) {
    @Override
    public Float intToFloat(int input) {
      return input / 255.0f;
    }

    @Override
    public Integer floatToInt(Float input) {
      return Math.round(input * 255.0f);
    }
  },
  SHORT(5122, 2) {
    @Override
    public Float intToFloat(int input) {
      return Math.max(input / 32767.0f, -1.0f);
    }

    @Override
    public Integer floatToInt(Float input) {
      return Math.round(input * 32767.0f);
    }
  },
  UNSIGNED_SHORT(5123, 2) {
    @Override
    public Float intToFloat(int input) {
      return input / 65535.0f;
    }

    @Override
    public Integer floatToInt(Float input) {
      return Math.round(input * 65535.0f);
    }
  },
  UNSIGNED_INT(5125, 4),
  FLOAT(5126, 4);

  private static final Map<Integer, GLTFAccessorSubDataType> valueMap = new HashMap<>();

  static {
    for (GLTFAccessorSubDataType type : GLTFAccessorSubDataType.values()) {
      valueMap.put(type.getValue(), type);
    }
  }

  private Integer value;
  private Integer sizeInBytes;

  GLTFAccessorSubDataType(int value, Integer sizeInBytes) {
    this.value = value;
    this.sizeInBytes = sizeInBytes;
  }

  static GLTFAccessorSubDataType getType(int value) {
    return valueMap.get(value);
  }

  public static Map<Integer, GLTFAccessorSubDataType> getValueMap() {
    return valueMap;
  }

  Integer getSizeInBytes() {
    return this.sizeInBytes;
  }

  Integer getValue() {
    return this.value;
  }

  public Float intToFloat(int input) {
    return (float) input;
  }

  public Integer floatToInt(Float input) {
    return input.intValue();
  }
}
