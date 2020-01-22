/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RenderEnvironmentMap {

  private String extension = ".hdr";
  private URI imagesFolder;

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

    //TODO push samplers? Not sure why.

    //u_DiffuseEnvSampler faces
    for (Entry<String, Integer> entry : cubeMapSides.entrySet()) {
      String imagePath = diffusePrefix + entry.getKey() + diffuseSuffix;

    }


  }

}
