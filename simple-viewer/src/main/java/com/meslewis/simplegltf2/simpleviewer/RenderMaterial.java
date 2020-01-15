/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFMaterial;
import com.meslewis.simplegltf2.data.GLTFPBRMetallicRoughness;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderMaterial {

  private static final Logger logger = LoggerFactory.getLogger(RenderMaterial.class);

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

    if (material.getOcclusionTexture() != null) {
      texturesMap.put("u_OcclusionSampler", new RenderTexture(material.getOcclusionTexture()));
      defines.add("HAS_OCCLUSION_MAP 1");
      properties.put("u_OcclusionStrength", material.getOcclusionTexture().getStrength());
      properties.put("u_OcclusionUVSet", (float) material.getOcclusionTexture().getTexCoord());
    }

    if (material.getEmissiveTexture() != null) {
      texturesMap.put("u_EmissiveSampler", new RenderTexture(material.getEmissiveTexture()));
      defines.add("HAS_EMISSIVE_MAP 1");
//      properties.put("u_EmissiveFactor", material.getEmissiveFactor());
      properties.put("u_EmissiveUVSet", (float) material.getEmissiveTexture().getTexCoord());
    }
    if (material.getPbrMetallicRoughness() != null) {
      GLTFPBRMetallicRoughness pbr = material.getPbrMetallicRoughness();

      //Base color texture
      if (pbr.getBaseColorTexture() != null) {
        texturesMap.put("u_BaseColorSampler", new RenderTexture(pbr.getBaseColorTexture()));
        defines.add("HAS_BASE_COLOR_MAP 1");
        properties.put("u_BaseColorUVSet", (float) pbr.getBaseColorTexture().getTexCoord());
      }

      //metallicRoughnessTexture
      if (pbr.getMetallicRoughnessTexture() != null) {
        texturesMap.put("u_MetallicRoughnessSampler",
            new RenderTexture(pbr.getMetallicRoughnessTexture()));
        defines.add("HAS_METALLIC_ROUGHNESS_MAP 1");
        properties.put("u_MetallicRoughnessUVSet",
            (float) pbr.getMetallicRoughnessTexture().getTexCoord());
      }

      //TODO diffuseTexture - I think its an extension
      //TODO specularGlossinessTexture - I think its an extension

//      defines.add("MATERIAL_METALLICROUGHNESS 1");
//      properties.put("u_BaseColorFactor", material.getPbrMetallicRoughness().getBaseColorFactor());
//      properties.put("u_MetallicFactor", material.getPbrMetallicRoughness().getMetallicFactor());
//      properties.put("u_RoughnessFactor", material.getPbrMetallicRoughness().getRoughnessFactor());
    }

    logger.info("End RenderMaterial init");

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
