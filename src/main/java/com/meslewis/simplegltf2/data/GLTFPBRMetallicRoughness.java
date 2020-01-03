/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * A set of parameter values that are used to define the metallic-roughness material model from
 * Physically-Based Rendering (PBR) methodology.
 */
public class GLTFPBRMetallicRoughness extends GLTFProperty {

  /**
   * The RGBA components of the base color of the material. The fourth component (A) is the alpha
   * coverage of the material. The `alphaMode` property specifies how alpha is interpreted. These
   * values are linear. If a baseColorTexture is specified, this value is multiplied with the texel
   * values.
   */
  @JsonProperty("baseColorFactor")
  private Float[] baseColorFactor = {1.0f, 1.0f, 1.0f, 1.0f};

  /**
   * The base color texture. The first three components (RGB) are encoded with the sRGB transfer
   * function. They specify the base color of the material. If the fourth component (A) is present,
   * it represents the linear alpha coverage of the material. Otherwise, an alpha of 1.0 is assumed.
   * The `alphaMode` property specifies how alpha is interpreted. The stored texels must not be
   * premultiplied.
   */
  @JsonProperty("baseColorTexture")
  private GLTFTextureInfo baseColorTexture;

  /**
   * The metalness of the material. A value of 1.0 means the material is a metal. A value of 0.0
   * means the material is a dielectric. Values in between are for blending between metals and
   * dielectrics such as dirty metallic surfaces. This value is linear. If a
   * metallicRoughnessTexture is specified, this value is multiplied with the metallic texel
   * values.
   */
  @JsonProperty("metallicFactor")
  @Max(1)
  @Min(0)
  private Float metallicFactor = 1.0f;

  /**
   * The roughness of the material. A value of 1.0 means the material is completely rough. A value
   * of 0.0 means the material is completely smooth. This value is linear. If a
   * metallicRoughnessTexture is specified, this value is multiplied with the roughness texel
   * values.
   */
  @JsonProperty("roughnessFactor")
  @Max(1)
  @Min(0)
  private Float roughnessFactor = 1.0f;

  /**
   * The metallic-roughness texture. The metalness values are sampled from the B channel. The
   * roughness values are sampled from the G channel. These values are linear. If other channels are
   * present (R or A), they are ignored for metallic-roughness calculations.
   */
  @JsonProperty("metallicRoughnessTexture")
  private GLTFTextureInfo metallicRoughnessTexture;

  public Float[] getBaseColorFactor() {
    return baseColorFactor;
  }

  public GLTFTextureInfo getBaseColorTexture() {
    return baseColorTexture;
  }

  public Float getMetallicFactor() {
    return metallicFactor;
  }

  public Float getRoughnessFactor() {
    return roughnessFactor;
  }

  public GLTFTextureInfo getMetallicRoughnessTexture() {
    return metallicRoughnessTexture;
  }
}
