/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

import com.meslewis.simplegltf2.data.GLTFSampler;
import com.meslewis.simplegltf2.data.GLTFTextureInfo;
import com.meslewis.simplegltf2.defaultImplementation.DefaultBufferIO;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;
import org.lwjgl.system.MemoryStack;

public class RenderTexture {

  private int mipLevel = 0;
  private GLTFSampler sampler;
  private Supplier<ByteBuffer> getData;
  private int glTexture = -1;
  private int type = GL_TEXTURE_2D; //TODO I think type should be called target
  private boolean initialized = false;

  private ByteBuffer data;
  private int width = -1;
  private int height = -1;

  public RenderTexture(GLTFTextureInfo info) {
    this.sampler = info.getTexture().getSampler();
    getData = info.getTexture().getSourceImage()::getDirectByteBuffer;
  }

  //Initialize a texture not referenced by the glTF file
  public RenderTexture(URI imagePath, int type) {
    this(imagePath, type, 0);
  }

  public RenderTexture(URI imagePath, int type, int mipLevel) {
    if (imagePath != null) {
      getData = () -> new DefaultBufferIO().getDirectByteBuffer(imagePath);
    }
    this.type = type;
    this.mipLevel = mipLevel;
  }

  public int getGlTexture() {
    if (glTexture < 0) {
      this.glTexture = glGenTextures();
    }
    return glTexture;
  }

  public GLTFSampler getSampler() {
    return sampler;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

  public ByteBuffer loadData() {
    if (data == null) {
      ByteBuffer dataBuffer = getData.get();

      try (MemoryStack stack = MemoryStack.stackPush()) {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);

        stbi_set_flip_vertically_on_load(false);
        //TODO not sure how different this buffer is from dataBuffer
        data = stbi_load_from_memory(dataBuffer, w, h, comp, 4);
        //TODO leaking memory ^
        this.width = w.get();
        this.height = h.get();
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

  public boolean shouldGenerateMips() {
    int width = getTextureWidth();
    int height = getTextureHeight();
    //If an int is a power of 2 then only its highest bit is set.
    //Subtracting one sets all lower bits to one, therefore there is no overlap.
    return (width > 0 && (width & width - 1) == 0) && (height > 0 && (height & height - 1) == 0);

  }

  public int getMipLevel() {
    return mipLevel;
  }
}
