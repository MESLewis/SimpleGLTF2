/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//Inspiration from shader_cache.js

/**
 * Shader programs vary depending on attributes in a GLTFMeshPrimitive The shader files that
 * glTF-Sample-Viewer uses take configurations as #define [name] before the shader code.
 * <p>
 * This class looks at a GLTFMeshPrimitive and returns a correct ShaderProgram to render it.
 */
public class ShaderCache {

  /**
   * Shader name -> source code
   */
  private static Map<String, String> sources;
  /**
   * name & permutations hashed -> compiled shader
   */
  private static Map<Integer, Integer> shaders = new HashMap<>();
  /**
   * (vertex shader, fragment shader) -> program
   */
  private static Map<String, ShaderProgram> programs = new HashMap<>();

  static {
    sources = new HashMap<>();
    //Set up shader sources
    sources.put("primitive.vert", IOUtil.getResouce("/default/shaders/primitive.vert"));
    sources.put("metallic-roughness.frag",
        IOUtil.getResouce("/default/shaders/metallic-roughness.frag"));
    sources.put("tonemapping.glsl", IOUtil.getResouce("/default/shaders/tonemapping.glsl"));
    sources.put("textures.glsl", IOUtil.getResouce("/default/shaders/textures.glsl"));
    sources.put("functions.glsl", IOUtil.getResouce("/default/shaders/functions.glsl"));
    sources.put("animation.glsl", IOUtil.getResouce("/default/shaders/animation.glsl"));

    //key, source
    for (Entry<String, String> entry : sources.entrySet()) {
      boolean changed = false;
      String src = entry.getValue();

      //includeName, includeSource
      for (Entry<String, String> includeEntry : sources.entrySet()) {
        String pattern = "#include <" + includeEntry.getKey() + ">";

        if (src.contains(pattern)) {
          src = src.replace(pattern, includeEntry.getValue());
          src.replaceAll(pattern, "");
          changed = true;
        }
      }
      if (changed) {
        sources.replace(entry.getKey(), src);
      }
    }
  }


  /**
   * eg "pbr.vert" ["NORMALS", "TANGENTS"]
   *
   * @param shaderIdentifier
   * @param permutationDefines
   * @return
   */
  public static int selectShader(String shaderIdentifier, List<String> permutationDefines) {
    String src = sources.get(shaderIdentifier);
    if (src == null) {
      System.err.println("Shader source for " + shaderIdentifier + " not found!");
      return -1;
    }

    boolean isVert = shaderIdentifier.endsWith(".vert");
    int hash = shaderIdentifier.hashCode();

    StringBuilder sb = new StringBuilder();
    for (String define : permutationDefines) {
      sb.append("#define ").append(define).append('\n');
      hash ^= define.hashCode();
    }

    if (!shaders.containsKey(hash)) {
      int shader = GlUtil.compileShader(shaderIdentifier, isVert, sb.toString() + src);
      shaders.put(hash, shader);
    }
    return hash;
  }

  public static ShaderProgram getShaderProgram(int vertexShaderHash, int fragmentShaderHash) {
    int hash = fragmentShaderHash ^ vertexShaderHash; //Yeah this probably will have problems

    if (programs.containsKey(hash)) {
      return programs.get(hash);
    }

    int linkedProg = GlUtil
        .linkProgram(shaders.get(vertexShaderHash), shaders.get(fragmentShaderHash));

    return new ShaderProgram(linkedProg, hash);
  }

}
