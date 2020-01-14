/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

//Inspiration from shader.js and LWJGL3 book
public class ShaderProgram {

  private int programId;
  private int hash;
  private Map<String, UniformData> uniforms = new HashMap<>();
  private Map<String, Integer> attributes = new HashMap<>();

  public ShaderProgram(int programId, int hash) {
    this.programId = programId;
    this.hash = hash;

    IntBuffer sizeB = BufferUtils.createIntBuffer(1);
    IntBuffer typeB = BufferUtils.createIntBuffer(1);

    int uniformCount = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);
    int strLen = glGetProgrami(programId, GL_ACTIVE_UNIFORM_MAX_LENGTH);
    for (int i = 0; i < uniformCount; ++i) {
      sizeB.rewind();
      typeB.rewind();
      String info = glGetActiveUniform(programId, i, strLen, sizeB, typeB);
      int loc = glGetUniformLocation(programId, info);
      //TODO I think I need to keep the type as well
      uniforms.put(info, new UniformData(loc, typeB.get()));
    }

    int attribCount = glGetProgrami(programId, GL_ACTIVE_ATTRIBUTES);
    strLen = glGetProgrami(programId, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
    for (int i = 0; i < attribCount; ++i) {
      sizeB.rewind();
      typeB.rewind();
      String info = glGetActiveAttrib(programId, i, strLen, sizeB, typeB);
      int loc = glGetAttribLocation(programId, info);
      attributes.put(info, loc);
    }
  }

  public int getProgramId() {
    return programId;
  }

  public void setUniform(String uniformName, Matrix4f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      // Dump matrix into float buffer
      FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(uniforms.get(uniformName).loc, false, fb);
    }
  }

  public void setUniform(String uniformName, Matrix3f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer fb = stack.mallocFloat(9);
      value.get(fb);
      glUniformMatrix3fv(uniforms.get(uniformName).loc, false, fb);
    }
  }

  public void setUniform(String uniformName, Vector3f value) {
    glUniform3f(uniforms.get(uniformName).loc, value.x, value.y, value.z);
  }

  public void setUniform(String uniformName, Vector4f value) {
    glUniform4f(uniforms.get(uniformName).loc, value.x, value.y, value.z, value.w);
  }

  public void setUniform(String uniformName, int value) {
    glUniform1i(uniforms.get(uniformName).loc, value);
  }

  public void setUniform(String uniformName, float value) {
    glUniform1f(uniforms.get(uniformName).loc, value);
  }

  public int getUniformLocation(String uniformName) {
    return uniforms.get(uniformName).loc;
  }

  public int getAttributeLocation(String name) {
    Integer loc = attributes.get(name);
    if (loc == null) {
      int test = glGetAttribLocation(programId, name);
      System.err.println("Attribute " + name + " does not exist");
      return -1;
    }
    return loc;
  }

  private class UniformData {

    int loc;
    int type;

    UniformData(int loc, int type) {
      this.loc = loc;
      this.type = type;
    }
  }
}
