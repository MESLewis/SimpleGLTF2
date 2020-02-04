/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer.render;

import com.meslewis.simplegltf2.data.GLTFAlphaMode;
import com.meslewis.simplegltf2.data.GLTFMaterial;
import com.meslewis.simplegltf2.data.GLTFPBRMetallicRoughness;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderMaterial {

  private static final Logger logger = LoggerFactory.getLogger(RenderMaterial.class);

  public static final RenderMaterial defaultMaterial;

  static {
    GLTFMaterial defaultGLTFMaterial = new GLTFMaterial();
    defaultMaterial = new RenderMaterial(defaultGLTFMaterial);

    defaultMaterial.type = "MR";
    defaultMaterial.defines.add("MATERIAL_METALLICROUGHNESS 1");
    defaultMaterial.properties.put("u_BaseColorFactor", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
    defaultMaterial.properties.put("u_MetallicFactor", 1.0f);
    defaultMaterial.properties.put("u_RoughnessFactor", 1.0f);
  }

  private String type = "MR";
  private Map<String, RenderTexture> texturesMap = new HashMap<>();
  private Map<String, Object> properties = new HashMap<>();

  private List<String> defines = new ArrayList<>();
  private GLTFMaterial material;

  public RenderMaterial(GLTFMaterial material) {
    this.material = material;

    //https://github.com/KhronosGroup/glTF-Sample-Viewer/blob/a18868cfe652bab4c084c751c80a6cfb55ae0f2f/src/material.js#L121

    if (material.getNormalTexture() != null) {
      texturesMap.put("u_NormalSampler", new RenderTexture(material.getNormalTexture()));
      defines.add("HAS_NORMAL_MAP 1");
      properties.put("u_NormalScale", material.getNormalTexture().getScale());
      properties.put("u_NormalUVSet", material.getNormalTexture().getTexCoord());
      logger.debug("Material normal map set " + material.toString());
    }

    if (material.getOcclusionTexture() != null) {
      texturesMap.put("u_OcclusionSampler", new RenderTexture(material.getOcclusionTexture()));
      defines.add("HAS_OCCLUSION_MAP 1");
      properties.put("u_OcclusionStrength", material.getOcclusionTexture().getStrength());
      properties.put("u_OcclusionUVSet", material.getOcclusionTexture().getTexCoord());
      logger.debug("Material occlusion map set " + material.toString());
    }

    if (material.getEmissiveTexture() != null) {
      texturesMap.put("u_EmissiveSampler", new RenderTexture(material.getEmissiveTexture()));
      defines.add("HAS_EMISSIVE_MAP 1");
      float[] emissive = material.getEmissiveFactor();
      Vector3f emissiveFactor = new Vector3f(emissive[0], emissive[1], emissive[2]);
      properties.put("u_EmissiveFactor", emissiveFactor);
      properties.put("u_EmissiveUVSet", material.getEmissiveTexture().getTexCoord());
      logger.debug("Material emissive map set " + material.toString());
    }
    if (material.getPbrMetallicRoughness() != null) {
      GLTFPBRMetallicRoughness pbr = material.getPbrMetallicRoughness();

      //Base color texture
      if (pbr.getBaseColorTexture() != null) {
        texturesMap.put("u_BaseColorSampler", new RenderTexture(pbr.getBaseColorTexture()));
        defines.add("HAS_BASE_COLOR_MAP 1");
        properties.put("u_BaseColorUVSet", pbr.getBaseColorTexture().getTexCoord());
        logger.debug("Material base color map set " + material.toString());
      }

      //metallicRoughnessTexture
      if (pbr.getMetallicRoughnessTexture() != null) {
        texturesMap.put("u_MetallicRoughnessSampler",
            new RenderTexture(pbr.getMetallicRoughnessTexture()));
        defines.add("HAS_METALLIC_ROUGHNESS_MAP 1");
        properties.put("u_MetallicRoughnessUVSet", pbr.getMetallicRoughnessTexture().getTexCoord());
        logger.debug("Material metallic roughness map set " + material.toString());
      }

      //TODO diffuseTexture - extension
      //TODO specularGlossinessTexture - extension

      if (!this.type.equals("SG")) {
        defines.add("MATERIAL_METALLICROUGHNESS 1");
        float[] bcff = pbr.getBaseColorFactor();
        Vector4f baseColorFactor = new Vector4f(bcff[0], bcff[1], bcff[2], bcff[3]);
        properties.put("u_BaseColorFactor", baseColorFactor);
        properties.put("u_MetallicFactor",
            material.getPbrMetallicRoughness().getMetallicFactor()); //ORIGINAL
        properties.put("u_RoughnessFactor",
            material.getPbrMetallicRoughness().getRoughnessFactor()); //ORIGINAL
      }
    }

    if (this.getGLTFMaterial().getAlphaMode() == GLTFAlphaMode.MASK) {
      defines.add("ALPHAMODE_MASK 1");
      properties.put("u_AlphaCutoff", getGLTFMaterial().getAlphaCutoff());
    } else if (this.getGLTFMaterial().getAlphaMode() == GLTFAlphaMode.OPAQUE) {
      this.defines.add("ALPHAMODE_OPAQUE 1");
    }

    logger.info("End RenderMaterial init: " + material.toString());
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

  public Map<String, Object> getProperties() {
    return properties;
  }

  public Map<String, RenderTexture> getTexturesMap() {
    return texturesMap;
  }

  public GLTFMaterial getGLTFMaterial() {
    return material;
  }

  public GLTFAlphaMode getAlphaMode() {
    if (this.getGLTFMaterial() != null) {
      return this.getGLTFMaterial().getAlphaMode();
    } else {
      return GLTFAlphaMode.OPAQUE;
    }
  }
}
