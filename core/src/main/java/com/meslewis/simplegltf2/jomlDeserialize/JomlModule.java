/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.jomlDeserialize;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JomlModule extends SimpleModule {

  private static final SimpleModule instance;

  static {
    instance = new JomlModule()
        .addDeserializer(Matrix4f.class, new Matrix4fDeserializer())
        .addDeserializer(Quaternionf.class, new QuaternionfDeserializer())
        .addDeserializer(Vector3f.class, new Vector3fDeserializer())
        .addDeserializer(Vector4f.class, new Vector4fDeserializer())
    ;

  }

  public static SimpleModule getModule() {
    return instance;
  }

}
