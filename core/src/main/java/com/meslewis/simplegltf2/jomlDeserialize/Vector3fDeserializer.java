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
import org.joml.Vector3f;

public class Vector3fDeserializer extends StdDeserializer<Vector3f> {

  protected Vector3fDeserializer(Class<?> vc) {
    super(vc);
  }

  protected Vector3fDeserializer() {
    this(null);
  }

  @Override
  public Vector3f deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    float[] values = p.readValueAs(float[].class);
    return new Vector3f(values[0], values[1], values[2]);
  }
}
