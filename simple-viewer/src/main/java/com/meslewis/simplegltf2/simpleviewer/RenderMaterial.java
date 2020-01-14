/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFMaterial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderMaterial {

  private static final RenderMaterial defaultMaterial;

  static {
    GLTFMaterial defaultGLTFMaterial = new GLTFMaterial();
    defaultMaterial = new RenderMaterial(defaultGLTFMaterial);

    defaultMaterial.type = "MR";
//    defaultMaterial.name = "Default Material";
    defaultMaterial.defines.add("MATERIAL_METALLICROUGHNESS 1");
    defaultMaterial.properties.put("u_BaseColorFactor", 1.0f);
    defaultMaterial.properties.put("u_MetllicFactor", 1.0f);
    defaultMaterial.properties.put("u_RoughnessFactor", 1.0f);
  }

  private String type = "unlit";
  private Map<String, RenderTexture> texturesMap = new HashMap<>();
  private Map<String, Float> properties = new HashMap<>();

  private List<String> defines = new ArrayList<>();

  private GLTFMaterial material;

  public RenderMaterial(GLTFMaterial material) {
    this.material = material;

    //TODO https://github.com/KhronosGroup/glTF-Sample-Viewer/blob/a18868cfe652bab4c084c751c80a6cfb55ae0f2f/src/material.js#L121

    if (material.getNormalTexture() != null) {
      texturesMap.put("u_NormalSampler", new RenderTexture(material.getNormalTexture()));
      defines.add("HAS_NORMAL_MAP 1");
      properties.put("u_NormalScale", material.getNormalTexture().getScale());
      properties.put("u_NormalUVSet", Float.valueOf(material.getNormalTexture().getTexCoord()));
    }

    //TODO more types of textures
  }

  public String getShaderIdentifier() {
    switch (this.type) {
      case "SG": //Fall through until we separate shaders
      case "MR":
        return "metallic-roughness.frag";
      default:
        return "metallic-roughness.frag";
    }
  }

  public List<String> getDefines() {
    return defines;
  }

  public Map<String, Float> getProperties() {
    return properties;
  }

  public Map<String, RenderTexture> getTexturesMap() {
    return texturesMap;
  }
}
