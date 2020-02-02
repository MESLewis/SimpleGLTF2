/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLTFProperty {

  public static final Logger logger = LoggerFactory.getLogger(GLTFProperty.class);
  private final Map<String, JsonNode> extraValues = new HashMap<>();
  /**
   * Hold reference to parent gltf reference for linking
   */
  @JacksonInject
  GLTF gltf;
  /**
   * Dictionary object with extension-specific objects. TODO make this a catch all for extension
   * data, then it won't fail to deserialize
   */
  @JsonProperty("extensions")
  private Object extensions;
  /**
   * Application-specific data.
   */
  @JsonProperty("extras")
  private Object extras;

  @JsonAnySetter
  private void catchAllSetter(String name, JsonNode node) {
    logger.info("Unknown property: " + name + ", " + node);
    extraValues.put(name, node);
  }
}
