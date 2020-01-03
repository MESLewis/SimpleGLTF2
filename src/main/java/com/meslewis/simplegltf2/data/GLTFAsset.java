/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * Metadata about the glTF asset.
 */
public class GLTFAsset extends GLTFProperty {

  /**
   * A copyright message suitable for display to credit the content creator.
   */
  @JsonProperty("copyright")
  private String copyright;

  /**
   * Tool that generated this glTF model. Useful for debugging.
   */
  @JsonProperty("generator")
  private String generator;

  /**
   * The glTF version that this asset targets. TODO pattern "^[0-9]+\\.[0-9]+$"
   */
  @JsonProperty("version")
  @NotNull
  private String version;

  /**
   * The minimum glTF version that this asset targets. TODO pattern "^[0-9]+\\.[0-9]+$"
   */
  @JsonProperty("minVersion")
  private String minVersion;

  public String getCopyright() {
    return copyright;
  }

  public String getGenerator() {
    return generator;
  }

  public String getVersion() {
    return version;
  }

  public String getMinVersion() {
    return minVersion;
  }
}
