/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import com.meslewis.simplegltf2.data.GLTFTextureInfo;

public class RenderTexture {

  private GLTFTextureInfo info;
  private int glTexture = -1;
  private int type = GL_TEXTURE_2D;
  private boolean initialized = false;

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
}
