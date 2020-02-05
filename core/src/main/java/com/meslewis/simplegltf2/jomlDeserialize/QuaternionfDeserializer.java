/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.jomlDeserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.joml.Quaternionf;

public class QuaternionfDeserializer extends StdDeserializer<Quaternionf> {

  protected QuaternionfDeserializer(Class<?> vc) {
    super(vc);
  }

  protected QuaternionfDeserializer() {
    this(null);
  }

  @Override
  public Quaternionf deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    float[] value = p.readValueAs(float[].class);
    return new Quaternionf(value[0], value[1], value[2], value[3]);
  }
}
