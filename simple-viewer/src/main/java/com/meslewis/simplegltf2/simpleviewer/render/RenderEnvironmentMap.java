/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;

import com.meslewis.simplegltf2.simpleviewer.IOUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RenderEnvironmentMap {

  private String extension = ".hdr";
  private URI imagesFolder;
  private List<RenderTexture> diffuseEnvMap = new ArrayList<>();
  private List<RenderTexture> specularEnvMap = new ArrayList<>();
  private RenderTexture lut;

  private final int mipLevel = 0; //TODO higher mipmap level

  RenderEnvironmentMap(URI imagesFolder) {
    this.imagesFolder = imagesFolder;

    String diffusePrefix = "diffuse/diffuse_";
    String diffuseSuffix = "_0" + extension;
    String specularPrefix = "specular/specular_";
    String specularSuffix = "_";

    Map<String, Integer> cubeMapSides = new HashMap<>();

    cubeMapSides.put("right", GL_TEXTURE_CUBE_MAP_POSITIVE_X);
    cubeMapSides.put("left", GL_TEXTURE_CUBE_MAP_NEGATIVE_X);
    cubeMapSides.put("top", GL_TEXTURE_CUBE_MAP_POSITIVE_Y);
    cubeMapSides.put("bottom", GL_TEXTURE_CUBE_MAP_NEGATIVE_Y);
    cubeMapSides.put("front", GL_TEXTURE_CUBE_MAP_POSITIVE_Z);
    cubeMapSides.put("back", GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);

    //u_DiffuseEnvSampler faces
    for (Entry<String, Integer> entry : cubeMapSides.entrySet()) {
      String imagePath = diffusePrefix + entry.getKey() + diffuseSuffix;
      RenderTexture tex = new RenderTexture(URI.create(imagesFolder.toString() + imagePath),
          entry.getValue());
      diffuseEnvMap.add(tex);
    }
    //TODO I think I need a sampler for this one
    diffuseEnvMap.add(0, new RenderTexture(GL_TEXTURE_CUBE_MAP));

    //u_SpecularEnvSampler tex
    for (Entry<String, Integer> entry : cubeMapSides.entrySet()) {
      String imagePath = specularPrefix + entry.getKey() + specularSuffix;
      addSide(imagesFolder, imagePath, entry.getValue(), mipLevel);
    }
    specularEnvMap.add(0, new RenderTexture(GL_TEXTURE_CUBE_MAP));

    lut = new RenderTexture(IOUtil.getResource("images/brdfLUT.png"),
        GL_TEXTURE_2D);
  }

  private void addSide(URI rootURI, String basePath, int glSide, int mipLevel) {
    for (int i = 0; i <= mipLevel; i++) {
      String imagePath = basePath + i + extension;
      RenderTexture tex = new RenderTexture(URI.create(imagesFolder.toString() + imagePath),
          glSide);
      specularEnvMap.add(tex);
    }
  }

  public List<RenderTexture> getDiffuseEnvMap() {
    return diffuseEnvMap;
  }

  public List<RenderTexture> getSpecularEnvMap() {
    return specularEnvMap;
  }

  public RenderTexture getLut() {
    return lut;
  }
}
