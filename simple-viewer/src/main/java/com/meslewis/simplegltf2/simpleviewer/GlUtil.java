/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
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
import com.meslewis.simplegltf2.data.GLTFImage;
import com.meslewis.simplegltf2.data.GLTFSampler;
import com.meslewis.simplegltf2.data.GLTFTexture;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlUtil {

  private static final Logger logger = LoggerFactory.getLogger(GlUtil.class);

  private static final Map<GLTFAccessor, Integer> accessorGlBufferMap = new HashMap<>();

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

  public static void setTexture(int location, RenderTexture info) {
    logger.info("Begin setTexture");
    if (info.getGlTexture() < 0) {
      info.setGlTexture(glGenTextures());
    }

    GLTFTexture gltfTex = info.getInfo().getTexture();

    glActiveTexture(GL_TEXTURE0 + info.getInfo().getTexCoord());
    glBindTexture(info.getType(), info.getGlTexture());

    glUniform1i(location, info.getInfo().getTexCoord());

    if (!info.isInitialized()) {
      GLTFSampler sampler = gltfTex.getSampler();

      //TODO translate this from UNPACK_FLIP_Y_WEBGL
//      glPixelStorei(GL_UNPACK);

      GLTFImage image = gltfTex.getSourceImage();

      try {
        //TODO this is a literal guess
        glTexImage2D(info.getType(), 0, GL_RGBA, 100, 100, 1, GL_RGBA, GL_UNSIGNED_BYTE,
            MemoryUtil.memAddress(image.getData()));
      } catch (IOException e) {
        e.printStackTrace();
      }

      //TODO set sampler

      info.setInitialized(true);
      logger.info("End setTexture");
    }
  }
}
