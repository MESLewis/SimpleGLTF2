/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import org.joml.Vector3f;

public class GLTFUtil {

  public static Vector3f floatArrayToVector3f(float[] array) {
    return new Vector3f(array[0], array[1], array[2]);
  }

}
