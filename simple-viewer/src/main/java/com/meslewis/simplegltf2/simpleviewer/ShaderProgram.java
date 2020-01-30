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
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Inspiration from shader.js and LWJGL3 book
public class ShaderProgram {

  private static final Logger logger = LoggerFactory.getLogger(ShaderProgram.class);

  private int programId;
  private int hash;
  private Map<String, UniformData> uniforms = new HashMap<>();
  private Map<String, Integer> attributes = new HashMap<>();

  private List<String> unknownAttributes = new ArrayList<>();
  private List<String> unknownUniforms = new ArrayList<>();

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
      uniforms.put(info, new UniformData(loc, typeB.get()));
      logger.debug("ShaderProgram = " + programId + ", uniform = " + info + ", " + loc);
    }

    int attribCount = glGetProgrami(programId, GL_ACTIVE_ATTRIBUTES);
    strLen = glGetProgrami(programId, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH);
    for (int i = 0; i < attribCount; ++i) {
      sizeB.rewind();
      typeB.rewind();
      String info = glGetActiveAttrib(programId, i, strLen, sizeB, typeB);
      int loc = glGetAttribLocation(programId, info);
      attributes.put(info, loc);
      logger.debug("ShaderProgram = " + programId + ", attribute = " + info + ", " + loc);
    }
  }

  public int getProgramId() {
    return programId;
  }

  public void setUniform(String uniformName, Matrix4f value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      try (MemoryStack stack = MemoryStack.stackPush()) {
        // Dump matrix into float buffer
        FloatBuffer fb = stack.mallocFloat(16);
        value.get(fb);
        glUniformMatrix4fv(loc, false, fb);
      }
    }
  }

  public void setUniform(String uniformName, Matrix3f value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      try (MemoryStack stack = MemoryStack.stackPush()) {
        FloatBuffer fb = stack.mallocFloat(9);
        value.get(fb);
        glUniformMatrix3fv(loc, false, fb);
      }
    }
  }

  public void setUniform(String uniformName, Vector4f value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      glUniform4f(loc, value.x, value.y, value.z, value.w);
    }
  }

  public void setUniform(String uniformName, Vector3f value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      glUniform3f(loc, value.x, value.y, value.z);
    }
  }

  public void setUniform(String uniformName, Vector2f value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      glUniform2f(loc, value.x, value.y);
    }
  }

  public void setUniform(String uniformName, float value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      glUniform1f(loc, value);
    }
  }

  public void setUniform(String uniformName, int value) {
    int loc = getUniformLocation(uniformName);
    if (loc > -1) {
      glUniform1i(loc, value);
    }
  }

  public void setUniform(String uniformName, float[] value) {
    int loc = getUniformLocation(
        uniformName + "[0]"); //Only the [0] of an array is stored in uniform list
    if (loc > -1) {
      glUniform1fv(loc, value);
    }
  }

  public void setUniform(String uniformName, Object value) {
    if (value instanceof Float) {
      setUniform(uniformName, (float) value);
      return;
    }
    if (value instanceof Vector4f) {
      setUniform(uniformName, (Vector4f) value);
      return;
    }
    if (value instanceof Vector3f) {
      setUniform(uniformName, (Vector3f) value);
      return;
    }
    if (value instanceof Vector2f) {
      setUniform(uniformName, (Vector2f) value);
      return;
    }
    if (value instanceof Integer) {
      setUniform(uniformName, (int) value);
      return;
    }
    if (value instanceof Object[]) {//We most likely don't want this to actually be called
      logger.warn("WARNING: setUniform is using an Object array, you probably don't want this");
      Object[] arr = (Object[]) value;
      int i = 0;
      for (Object obj : arr) {
        setUniform(uniformName + "[" + i + "]", obj);
        i++;
      }
      return;
    }
    logger.error("Unhandled type in setUniform: " + value.getClass());
  }

  public void setUniform(String structName, List<UniformLight> lightList) {
    Field[] fields = UniformLight.class.getDeclaredFields();
    int i = 0;
    for (UniformLight uniformLight : lightList) {
      for (Field field : fields) {
        String uniformName = structName + "[" + i + "]." + field.getName();
        try {
          setUniform(uniformName, field.get(uniformLight));
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
      i++;
    }
  }

  public int getUniformLocation(String uniformName) {
    UniformData data = uniforms.get(uniformName);

    if (data == null) {
      if (!unknownUniforms.contains(uniformName)) {
        unknownUniforms.add(uniformName);
        logger.info("Uniform " + uniformName + " does not exist");
      }
      return -1;
    }
    return data.loc;
  }

  public int getAttributeLocation(String name) {
    Integer loc = attributes.get(name);
    if (loc == null) {
      if (!unknownAttributes.contains(name)) {
        unknownAttributes.add(name);
        logger.info("Attribute " + name + " does not exist ");
      }
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
