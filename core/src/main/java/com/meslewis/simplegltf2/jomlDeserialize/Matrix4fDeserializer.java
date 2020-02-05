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
import org.joml.Matrix4f;

public class Matrix4fDeserializer extends StdDeserializer<Matrix4f> {

  protected Matrix4fDeserializer(Class<?> vc) {
    super(vc);
  }

  protected Matrix4fDeserializer() {
    this(null);
  }

  @Override
  public Matrix4f deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    return new Matrix4f().set(p.readValueAs(float[].class));
  }
}
