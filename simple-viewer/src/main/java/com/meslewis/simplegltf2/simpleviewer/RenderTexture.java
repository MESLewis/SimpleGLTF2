/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.meslewis.simplegltf2.data.GLTFTextureInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

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
        String mimeType = info.getTexture().getSourceImage().getMimeType();

        Iterator<ImageReader> readerIterator = ImageIO.getImageReadersByMIMEType(mimeType);

        //jpg doesn't return a reader if using mime type. Work around for now.
        //Definite room for improvement
        if (!readerIterator.hasNext()) {
          String uri = info.getTexture().getSourceImage().getURI().toString();
          String suffix = uri.substring(uri.lastIndexOf('.') + 1);
          readerIterator = ImageIO.getImageReadersBySuffix(suffix);
        }

        ImageReader imageReader = readerIterator.next();
        InputStream is = new ByteBufferBackedInputStream(dataBuffer);
        MemoryCacheImageInputStream imageInputStream = new MemoryCacheImageInputStream(is);
        imageReader.setInput(imageInputStream);

        this.width = imageReader.getWidth(0);
        this.height = imageReader.getHeight(0);

        data = info.getTexture().getSourceImage().getDirectByteBuffer();
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
