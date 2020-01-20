/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL30C.glBindFragDataLocation;

import java.util.ArrayList;
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

  private static class ShaderVars {

    public Integer glRef = -1;
    public List<String> varNames;

    ShaderVars(int glRef, List<String> varNames) {
      this.glRef = glRef;
      this.varNames = varNames;
    }
  }

  /**
   * Shader name -> source code
   */
  private static Map<String, String> sources;
  /**
   * name & permutations hashed -> compiled shader
   */
  private static Map<Integer, ShaderVars> shaders = new HashMap<>();
  /**
   * (vertex shader, fragment shader) -> program
   */
  private static Map<Integer, ShaderProgram> programs = new HashMap<>();

  static {
    sources = new HashMap<>();
    //Set up shader sources
    sources.put("primitive.vert", IOUtil.getResouce("/default/shaders/combined-primitive.vert"));
    sources.put("metallic-roughness.frag",
        IOUtil.getResouce("/default/shaders/combined-metallic-roughness.frag"));

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
    int hash = IOUtil.stringHash(shaderIdentifier);

    StringBuilder sb = new StringBuilder();
    sb.append("#version 330\n"); //Put this in so it doesn't give a warning
    for (String define : permutationDefines) {
      sb.append("#define ").append(define).append('\n');
      hash ^= IOUtil.stringHash(define);
    }

    String finalShaderCode = sb.toString() + src;

    if (!shaders.containsKey(hash)) {
      int shader = GlUtil.compileShader(shaderIdentifier, isVert, finalShaderCode);
      ShaderVars vars = new ShaderVars(shader, permutationDefines);
      shaders.put(hash, vars);
    }
    return hash;
  }

  public static ShaderProgram getShaderProgram(int vertexShaderHash, int fragmentShaderHash) {
    int hash = vertexShaderHash ^ fragmentShaderHash;

    if (programs.containsKey(hash)) {
      return programs.get(hash);
    }

    ShaderVars vertVars = shaders.get(vertexShaderHash);
    ShaderVars fragVars = shaders.get(fragmentShaderHash);

    int linkedProg = GlUtil.linkProgram(vertVars.glRef, fragVars.glRef);

    ShaderProgram program = new ShaderProgram(linkedProg, hash);

    //Initialize data locations
    int programId = program.getProgramId();

    glBindFragDataLocation(programId, 0, "fragColor"); //Personally defined always used
//
//    for(String fragName : fragVars.varNames) {
//      glBindFragDataLocation(programId, 0, fragName);
//    }
    programs.put(hash, program);

    return program;
  }

  public static ShaderProgram getDebugShaderProgram() {
    List<String> vertDefines = new ArrayList<>();
    List<String> fragDefines = new ArrayList<>();
    fragDefines.add("DEBUG 1");
    fragDefines.add("DEBUG_NORMALS 1");
    int vertHash = selectShader("primitive.vert", vertDefines);
    int fragHash = selectShader("metallic-roughness.frag", fragDefines);
    return getShaderProgram(vertHash, fragHash);
  }
}
