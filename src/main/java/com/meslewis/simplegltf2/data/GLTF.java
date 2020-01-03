/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * The root object fr a glTF asset
 */
public class GLTF extends GLTFProperty {

  @JsonAnySetter
  private final Map<String, JsonNode> extraValues = new HashMap<>();
  /**
   * Names of glTF extensions used somewhere in this asset.
   */
  @JsonProperty("extensionsUsed")
  private LinkedHashSet<String> extensionsUsed;
  /**
   * Names of glTF extensions required to properly load this asset.
   */
  @JsonProperty("extensionsRequired")
  private LinkedHashSet<String> extensionsRequired;
  /**
   * An array of accessors. An accessor is a typed view into a bufferView
   */
  @JsonProperty("accessors")
  private ArrayList<GLTFAccessor> accessors;
  /**
   * An array of keyframe animations.
   */
  @JsonProperty("animation")
  private ArrayList<GLTFAnimation> animations;
  /**
   * Metadata about the glTF asset.
   */
  @JsonProperty("asset")
  @NotNull
  private GLTFAsset asset;
  /**
   * An array of buffers. A buffer points to binary geometry, animation, or kins.
   */
  @JsonProperty("buffers")
  private ArrayList<GLTFBuffer> buffers;
  /**
   * An array of bufferViews.  A bufferView is a view into a buffer generally representing a subset
   * of the buffer.
   */
  @JsonProperty("bufferViews")
  private ArrayList<GLTFBufferView> bufferViews;
  /**
   * An array of cameras.  A camera defines a projection matrix.
   */
  @JsonProperty("cameras")
  private ArrayList<GLTFCamera> cameras;
  /**
   * An array of images.  An image defines data used to create a texture.
   */
  @JsonProperty("images")
  private ArrayList<GLTFImage> images;
  /**
   * An array of materials. A material defines the appearance of a primitive.
   */
  @JsonProperty("materials")
  private ArrayList<GLTFMaterial> materials;
  /**
   * An array of meshes. A mes is a set of primitives to be rendered.
   */
  @JsonProperty("meshes")
  private ArrayList<GLTFMesh> meshes;
  /**
   * An array of nodes.
   */
  @JsonProperty("nodes")
  private ArrayList<GLTFNode> nodes;
  /**
   * An array of samplers.  A sampler contains properties for texture filtering and wrapping modes.
   */
  @JsonProperty("samplers")
  private ArrayList<GLTFSampler> samplers;
  /**
   * The index of the default scene.
   */
  @JsonProperty("scene")
  private Integer indexDefaultScene;
  /**
   * An array of scenes.
   */
  @JsonProperty("scenes")
  private ArrayList<GLTFScene> scenes;
  /**
   * An array of skins. A skin is defined by joints and matrices.
   */
  @JsonProperty("skins")
  private ArrayList<GLTFSkin> skins;
  /**
   * An array of textures.
   */
  @JsonProperty("textures")
  private ArrayList<GLTFTexture> textures;

  public Map<String, JsonNode> getExtraValues() {
    return extraValues;
  }

  public LinkedHashSet<String> getExtensionsUsed() {
    return extensionsUsed;
  }

  public LinkedHashSet<String> getExtensionsRequired() {
    return extensionsRequired;
  }

  public ArrayList<GLTFAccessor> getAccessors() {
    return accessors;
  }

  public ArrayList<GLTFAnimation> getAnimations() {
    return animations;
  }

  public GLTFAsset getAsset() {
    return asset;
  }

  public ArrayList<GLTFBuffer> getBuffers() {
    return buffers;
  }

  public ArrayList<GLTFBufferView> getBufferViews() {
    return bufferViews;
  }

  public ArrayList<GLTFCamera> getCameras() {
    return cameras;
  }

  public ArrayList<GLTFImage> getImages() {
    return images;
  }

  public ArrayList<GLTFMaterial> getMaterials() {
    return materials;
  }

  public ArrayList<GLTFMesh> getMeshes() {
    return meshes;
  }

  public ArrayList<GLTFNode> getNodes() {
    return nodes;
  }

  public ArrayList<GLTFSampler> getSamplers() {
    return samplers;
  }

  public Integer getIndexDefaultScene() {
    return indexDefaultScene;
  }

  public ArrayList<GLTFScene> getScenes() {
    return scenes;
  }

  public ArrayList<GLTFSkin> getSkins() {
    return skins;
  }

  public ArrayList<GLTFTexture> getTextures() {
    return textures;
  }
}
