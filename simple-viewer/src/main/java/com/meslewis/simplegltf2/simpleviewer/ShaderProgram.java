/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

//Inspiration from shader.js and LWJGL3 book
public class ShaderProgram {

  private int programId;
  private int vertexShaderId;
  private int fragmentShaderId;
  private Map<String, Integer> uniforms;
  private Map<String, Integer> attributes;

  public ShaderProgram(String fragmentShader, String vertexShader) {
    programId = glCreateProgram();
    uniforms = new HashMap<>();
    attributes = new HashMap<>();
    createFragmentShader(fragmentShader);
    createVertexShader(vertexShader);
    try {
      link();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getProgramId() {
    return programId;
  }

  public void createVertexShader(String resource) {
    vertexShaderId = createShader(resource, GL_VERTEX_SHADER);
  }

  public void createFragmentShader(String resource) {
    fragmentShaderId = createShader(resource, GL_FRAGMENT_SHADER);
  }

  protected int createShader(String resource, int shaderType) {
    int shaderId = glCreateShader(shaderType);
    if (shaderId == 0) {
      throw new RuntimeException("Error creating shader. Type: " + shaderType);
    }

    ByteBuffer source = null;
    try {
      InputStream inputStream = ShaderProgram.class.getResourceAsStream(resource);
      byte[] inputArray = inputStream.readAllBytes();
      source = BufferUtils.createByteBuffer(inputArray.length);
      source.put(inputArray);
      source.rewind(); //Rewind is so dumb
    } catch (IOException e) {
      e.printStackTrace();
    }
    PointerBuffer strings = BufferUtils.createPointerBuffer(1);
    IntBuffer lengths = BufferUtils.createIntBuffer(1);
    strings.put(0, source);
    lengths.put(0, source.remaining());
    glShaderSource(shaderId, strings, lengths);
    glCompileShader(shaderId);

    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
      throw new RuntimeException(
          "Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
    }

    glAttachShader(programId, shaderId);

    return shaderId;
  }

  public void createUniform(String uniformName) throws Exception {
    int uniformLocation = glGetUniformLocation(programId, uniformName);
    if (uniformLocation < 0) {
      throw new Exception("Could not find uniform:" + uniformName);
    }
    uniforms.put(uniformName, uniformLocation);
  }

  public void createAttribute(String attribName) throws Exception {
    int attribLocation = glGetAttribLocation(programId, attribName);
    if (attribLocation < 0) {
      throw new Exception("Could not find attribute:" + attribName);
    }
    attributes.put(attribName, attribLocation);
  }

  public void setUniform(String uniformName, Matrix4f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      // Dump matrix into float buffer
      FloatBuffer fb = stack.mallocFloat(16);
      value.get(fb);
      glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
    }
  }

  public void setUniform(String uniformName, Matrix3f value) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer fb = stack.mallocFloat(9);
      value.get(fb);
      glUniformMatrix3fv(uniforms.get(uniformName), false, fb);
    }
  }

  public void setUniform(String uniformName, Vector3f value) {
    glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
  }

  public void setUniform(String uniformName, Vector4f value) {
    glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
  }

  public void setUniform(String uniformName, int value) {
    glUniform1i(uniforms.get(uniformName), value);
  }

  public void setUniform(String uniformName, float value) {
    glUniform1f(uniforms.get(uniformName), value);
  }

  public int getUniform(String uniformName) {
    return uniforms.get(uniformName);
  }

  public int getAttribute(String name) {
    return attributes.get(name);
  }

  public void link() throws Exception {
    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
    }

    if (vertexShaderId != 0) {
      glDetachShader(programId, vertexShaderId);
    }
    if (fragmentShaderId != 0) {
      glDetachShader(programId, fragmentShaderId);
    }

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
      System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
    }
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }
}
