/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL20.glUniform1i;

import com.meslewis.simplegltf2.data.GLTFImage;
import com.meslewis.simplegltf2.data.GLTFSampler;
import com.meslewis.simplegltf2.data.GLTFTexture;

public class RenderTexture {

  private GLTFTexture texture;
  private int glTexture;

  public RenderTexture(GLTFTexture texture, int loc, int texSlot) {
    this.texture = texture;

    glTexture = glGenTextures();
    //TODO May need to be variable?
    glBindTexture(GL_TEXTURE_2D, glTexture);

    glUniform1i(loc, texSlot);

    GLTFSampler sampler = texture.getSampler();

    GLTFImage image = texture.getSourceImage();

    //TODO loading texture

  }

  public int getGlTexture() {
    return glTexture;
  }

  public void setGlTexture(int glTexture) {
    this.glTexture = glTexture;
  }
}
