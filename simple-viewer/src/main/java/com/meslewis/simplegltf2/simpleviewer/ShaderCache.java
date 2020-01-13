/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.util.HashMap;
import java.util.Map;

//Inspiration from shader_cache.js
public class ShaderCache {

  private Map<Integer, ShaderProgram> programs = new HashMap<>();

  public ShaderProgram getShaderProgram(int fragmentShaderHash, int vertexShaderHash) {
    int hash = fragmentShaderHash + vertexShaderHash; //Yeah this probably will have problems

    ShaderProgram shader = programs.get(hash);

    return shader;

//    shader = new ShaderProgram();

  }
}
