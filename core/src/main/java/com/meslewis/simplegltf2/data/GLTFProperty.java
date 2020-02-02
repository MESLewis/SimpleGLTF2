/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLTFProperty {

  public static final Logger logger = LoggerFactory.getLogger(GLTFProperty.class);
  /**
   * Hold reference to parent gltf reference for linking
   */
  @JacksonInject
  GLTF gltf;
  /**
   * Dictionary object with extension-specific objects.
   */
  @JsonProperty("extensions")
  private Map<String, Object> extensions;
  /**
   * Application-specific data.
   */
  @JsonProperty("extras")
  private Map<String, Object> extras;

  public Map<String, Object> getExtensions() {
    if (extensions == null) {
      extensions = new HashMap<>();
    }
    return extensions;
  }

  public Map<String, Object> getExtras() {
    if (extras == null) {
      extras = new HashMap<>();
    }
    return extras;
  }
}
