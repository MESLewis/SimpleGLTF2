/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.nglBufferData;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFSampler;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlUtil {

  private static final Logger logger = LoggerFactory.getLogger(GlUtil.class);

  private static final Map<GLTFAccessor, Integer> accessorGlBufferMap = new HashMap<>();
  private static int max = glGetInteger(GL_MAX_TEXTURE_SIZE);

  public static int compileShader(String shaderIdentifier, boolean isVert, String shaderSource) {
    logger.info("Begin compileShader: " + shaderIdentifier);
//    logger.debug(shaderSource);
    int shader = glCreateShader(isVert ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
    glShaderSource(shader, shaderSource);
    glCompileShader(shader);

    int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);

    if (compiled < 1) {
      StringBuilder info = new StringBuilder();
      info.append(glGetShaderInfoLog(shader));
      logger
          .error("Could not compile OpenGL program " + shaderIdentifier + "\n\n" + info.toString());
    }
    logger.info("End comileShader: " + shaderIdentifier);
    return shader;
  }

  public static int linkProgram(int vertex, int fragment) {
    logger.info("Begin linkProgram: vertex = " + vertex + ", fragment = " + fragment);
    int program = glCreateProgram();
    glAttachShader(program, vertex);
    glAttachShader(program, fragment);

    glLinkProgram(program);

    if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
      String info = glGetProgramInfoLog(program);
      logger.error("Could not link OpenGL program \n\n" + info);
    }

    //Kinda useless to have here
    glValidateProgram(program);
    if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
      String info = glGetProgramInfoLog(program);
      logger.error("Error validating program \n\n" + info);
    }
    logger.info("End linkProgram: vertex = " + vertex + ", fragment = " + fragment + ", program = "
        + program);
    return program;
  }

  public static void setIndices(GLTFAccessor accessor) {
    if (!accessorGlBufferMap.containsKey(accessor)) {
      int glBuffer = glGenBuffers();
      accessorGlBufferMap.put(accessor, glBuffer);

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glBuffer);
      logger.debug("Using ByteBuffer memAddress");
      nglBufferData(GL_ELEMENT_ARRAY_BUFFER,
          accessor.getSizeInBytes(),
          MemoryUtil.memAddress(accessor.getData()),
          GL_STATIC_DRAW);
    } else {
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, accessorGlBufferMap.get(accessor));
    }
  }

  public static void enableAttribute(int attributeLocation, GLTFAccessor accessor) {
//    logger.debug("Begin enableAttribute: location = " + attributeLocation);
    if (!accessorGlBufferMap.containsKey(accessor)) {
      logger.debug("Generating buffer: " + accessor.toString());
      int glBuffer = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
      logger.debug("Using ByteBuffer memAddress");
      nglBufferData(GL_ARRAY_BUFFER,
          accessor.getSizeInBytes(),
          MemoryUtil.memAddress(accessor.getData()),
          GL_STATIC_DRAW);
      accessorGlBufferMap.put(accessor, glBuffer);
    } else {
      glBindBuffer(GL_ARRAY_BUFFER, accessorGlBufferMap.get(accessor));
    }

    glVertexAttribPointer(attributeLocation, accessor.getDataType().getPrimitiveCount(),
        accessor.getGLType(), accessor.isNormalized(), accessor.getByteStride(), 0);
    glEnableVertexAttribArray(attributeLocation);
//    logger.debug("End enableAttribute: location = " + attributeLocation);
  }

  public static boolean setTexture(int location, RenderTexture renderTexture, int texSlot) {
    return setTexture(location, renderTexture, texSlot, true);
  }

  private static boolean setTexture(int location, RenderTexture renderTexture, int texSlot,
      boolean useSampler) {
    if (renderTexture == null) {
      logger.warn("Texture is undefined: " + renderTexture.toString());
      return false;
    }

    //Activate a texture slot for new texture
    glActiveTexture(GL_TEXTURE0 + texSlot);
    //Bind renderTexture to slot
    glBindTexture(renderTexture.getType(), renderTexture.getGlTexture());

    //Load texture location into shader uniform
    glUniform1i(location, texSlot);

    if (!renderTexture.isInitialized()) {
      logger.info("Begin init texture");

      GLTFSampler sampler = renderTexture.getSampler();
      if (useSampler) {
        if (sampler == null) {
          logger.warn("Sampler is undefined for texture: " + renderTexture.toString());
          return false;
        }
      }

      loadImageToTexture(renderTexture);

      //TODO mipmaps

      if (useSampler) {
        int wrapS = sampler.getWrapS().getValue();
        int wrapT = sampler.getWrapT().getValue();
        int minFilter = sampler.getMinFilter().getValue();
        int maxFilter = sampler.getMagFilter().getValue();
        setSampler(wrapS, wrapT, minFilter, maxFilter, renderTexture.getType());
        renderTexture.setInitialized(true);
      }
      logger.info("End init texture");
    }
    return true;
  }

  private static void loadImageToTexture(RenderTexture renderTexture) {
    ByteBuffer buffer = renderTexture.loadData(); //Must load before width/height are available
    int type = renderTexture.getType();
    int width = renderTexture.getTextureWidth();
    int height = renderTexture.getTextureHeight();
    glTexImage2D(type, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
  }

  public static void setCubeMap(ShaderProgram shader, RenderEnvironmentMap envData, int texSlot) {
    List<RenderTexture> diffuseMap = envData.getDiffuseEnvMap();
    int wrapS = GL_CLAMP_TO_EDGE;
    int wrapT = GL_CLAMP_TO_EDGE;
    int minFilter = GL_LINEAR;
    int maxFilter = GL_LINEAR;

    applyCubeMapType(shader,
        "u_DiffuseEnvSampler",
        diffuseMap,
        texSlot++,
        wrapS,
        wrapT,
        minFilter,
        maxFilter);

    List<RenderTexture> specularMap = envData.getSpecularEnvMap();
    wrapS = GL_CLAMP_TO_EDGE;
    wrapT = GL_CLAMP_TO_EDGE;
    minFilter = GL_LINEAR_MIPMAP_LINEAR;
    maxFilter = GL_LINEAR;

    applyCubeMapType(shader,
        "u_SpecularEnvSampler",
        specularMap,
        texSlot++,
        wrapS,
        wrapT,
        minFilter,
        maxFilter);

    //Lut
    RenderTexture lut = envData.getLut();
    GlUtil.setTexture(shader.getUniformLocation("u_brdfLUT"), lut, texSlot++, false);
    setSampler(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_LINEAR, GL_LINEAR, lut.getType());
    lut.setInitialized(true);

    //TODO mipmaps
  }

  private static void applyCubeMapType(ShaderProgram shader,
      String uniform,
      List<RenderTexture> textures,
      int texSlot,
      int wrapS,
      int wrapT,
      int minFilter,
      int maxFilter) {
    int location = shader.getUniformLocation(uniform);
    for (RenderTexture renderTexture : textures) {
      //The cube map texture must be first
      if (renderTexture.getType() == GL_TEXTURE_CUBE_MAP) {
        //Activate a texture slot for new texture
        glActiveTexture(GL_TEXTURE0 + texSlot);

        //Bind renderTexture to slot
        glBindTexture(renderTexture.getType(), renderTexture.getGlTexture());

        //Load texture location into shader uniform
        glUniform1i(location, texSlot);

        renderTexture.setInitialized(true); //Set initialized so it doesn't try to load the texture
      }

      if (!renderTexture.isInitialized()) {
        logger.debug("Loading cubemap texture");
        loadImageToTexture(renderTexture);
        renderTexture.setInitialized(true);
      }
    }
    setSampler(wrapS, wrapT, minFilter, maxFilter, GL_TEXTURE_CUBE_MAP);
  }

  private static void setSampler(int wrapS, int wrapT, int minFilter, int maxFilter, int target) {
    glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
    glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);

    if (minFilter != GL_NEAREST && minFilter != GL_LINEAR) {
      if (minFilter == GL_NEAREST_MIPMAP_NEAREST || minFilter == GL_NEAREST_MIPMAP_LINEAR) {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      } else {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      }
    } else {
      glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
    }
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, maxFilter);
  }
}
