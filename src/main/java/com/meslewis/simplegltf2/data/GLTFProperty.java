/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GLTFProperty {

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

  /**
   * Hold reference to parent gltf reference for linking
   */
  @JacksonInject
  GLTF gltf;
}
