/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
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
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
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
import com.meslewis.simplegltf2.data.GLTFSampler;
import com.meslewis.simplegltf2.data.GLTFTexture;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.imageio.ImageIO;
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

  public static boolean setTexture(int location, RenderTexture renderTexture) {
    GLTFTexture gltfTex = renderTexture.getInfo().getTexture();

    if (gltfTex == null) {
      logger.warn("Texture is undefined: " + renderTexture.toString());
      return false;
    }

    if (renderTexture.getGlTexture() < 0) {
      renderTexture.setGlTexture(glGenTextures());
    }

    glActiveTexture(GL_TEXTURE0 + renderTexture.getInfo().getTexCoord());
    glBindTexture(renderTexture.getType(), renderTexture.getGlTexture());

    //Not really sure if this is right
    glUniform1i(location, renderTexture.getInfo().getTexCoord());

    if (!renderTexture.isInitialized()) {
      logger.info("Begin init texture");
      GLTFSampler sampler = gltfTex.getSampler();

      if (sampler == null) {
        logger.warn("Sampler is undefined for texture: " + renderTexture.getInfo().toString());
        return false;
      }

      renderTexture.loadData(); //debug, preloading so width/height are available

      int type = renderTexture.getType();
      int width = renderTexture.getTextureWidth();
      int height = renderTexture.getTextureHeight();

      try {
        ByteBuffer data = renderTexture.loadData();
        byte[] bufferArray = new byte[data.limit()]; //TODO hopefully this copy can be avoided
        data.get(bufferArray);
        BufferedImage debugImage = ImageIO.read(new ByteArrayInputStream(bufferArray));
        ByteBuffer buffer = convertImageData(debugImage);
        glTexImage2D(type, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
      } catch (IOException e) {
        e.printStackTrace();
      }

      //TODO mipmaps

      setSampler(sampler, renderTexture.getType());

      renderTexture.setInitialized(true);
      logger.info("End init texture");
    }
    return true;
  }

  private static void setSampler(GLTFSampler sampler, int type) {
    glTexParameteri(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    int minType = sampler.getMinFilter().getValue();
    if (minType != GL_NEAREST && minType != GL_LINEAR) {
      if (minType == GL_NEAREST_MIPMAP_NEAREST || minType == GL_NEAREST_MIPMAP_LINEAR) {
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
      } else {
        glTexParameteri(type, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      }
    } else {
      glTexParameteri(type, GL_TEXTURE_MIN_FILTER, minType);
    }
    glTexParameteri(type, GL_TEXTURE_MAG_FILTER, sampler.getMagFilter().getValue());
  }

  //Copied from LWJGL space invaders example
  //TODO this can likely be improved
  private static ByteBuffer convertImageData(BufferedImage bufferedImage) {
    ByteBuffer imageBuffer;
    WritableRaster raster;
    BufferedImage texImage;

    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
        .getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8},
        true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
        bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
    texImage = new BufferedImage(glAlphaColorModel, raster, true,
        new Hashtable());

    // copy the source image into the produced image
    Graphics g = texImage.getGraphics();
    g.setColor(new Color(0f, 0f, 0f, 0f));
    g.fillRect(0, 0, 256, 256);
    g.drawImage(bufferedImage, 0, 0, null);

    // build a byte buffer from the temporary image
    // that be used by OpenGL to produce a texture.
    byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
        .getData();

    imageBuffer = ByteBuffer.allocateDirect(data.length);
    imageBuffer.order(ByteOrder.nativeOrder());
    imageBuffer.put(data, 0, data.length);
    imageBuffer.flip();

    return imageBuffer;
  }
}
