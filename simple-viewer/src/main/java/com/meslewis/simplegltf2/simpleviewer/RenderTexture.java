/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import com.meslewis.simplegltf2.data.GLTFTextureInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

public class RenderTexture {

  private GLTFTextureInfo info;
  private int glTexture = -1;
  private int type = GL_TEXTURE_2D;
  private boolean initialized = false;

  private ByteBuffer data;
  private int width = -1;
  private int height = -1;


  public RenderTexture(GLTFTextureInfo info) {
    this.info = info;
  }

  public int getGlTexture() {
    return glTexture;
  }

  public void setGlTexture(int glTexture) {
    this.glTexture = glTexture;
  }

  public GLTFTextureInfo getInfo() {
    return info;
  }

  public int getType() {
    return type;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public ByteBuffer loadData() {
    if (data == null) {
      try {
        ByteBuffer dataBuffer = info.getTexture().getSourceImage().getDirectByteBuffer();

        try (MemoryStack stack = MemoryStack.stackPush()) {
          IntBuffer w = stack.mallocInt(1);
          IntBuffer h = stack.mallocInt(1);
          IntBuffer comp = stack.mallocInt(1);

          stbi_set_flip_vertically_on_load(false);
          //TODO not sure how different this buffer is from dataBuffer
          data = stbi_load_from_memory(dataBuffer, w, h, comp, 4);
          this.width = w.get();
          this.height = h.get();
        }
        return data;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return data;
  }

  public int getTextureWidth() {
    return width;
  }

  public int getTextureHeight() {
    return height;
  }
}
