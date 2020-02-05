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
import org.joml.Vector4f;

public class Vector4fDeserializer extends StdDeserializer<Vector4f> {

  protected Vector4fDeserializer(Class<?> vc) {
    super(vc);
  }

  protected Vector4fDeserializer() {
    this(null);
  }

  @Override
  public Vector4f deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    float[] values = p.readValueAs(float[].class);
    return new Vector4f(values[0], values[1], values[2], values[3]);
  }
}
