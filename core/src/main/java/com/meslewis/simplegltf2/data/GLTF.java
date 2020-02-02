/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meslewis.simplegltf2.GLTFImporter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The root object fr a glTF asset
 */
public class GLTF extends GLTFProperty {

  /**
   * Holds references of a field to set, an index to get, and a field to get from
   */
  List<Runnable> indexResolvers = new ArrayList<>();

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
  @Valid
  private List<GLTFAccessor> accessors;
  /**
   * An array of keyframe animations.
   */
  @JsonProperty("animations")
  @Valid
  private List<GLTFAnimation> animations;
  /**
   * Metadata about the glTF asset.
   */
  @JsonProperty("asset")
  @Valid
  @NotNull
  private GLTFAsset asset;
  /**
   * An array of buffers. A buffer points to binary geometry, animation, or kins.
   */
  @JsonProperty("buffers")
  @Valid
  private List<GLTFBuffer> buffers;
  /**
   * An array of bufferViews.  A bufferView is a view into a buffer generally representing a subset
   * of the buffer.
   */
  @JsonProperty("bufferViews")
  @Valid
  private List<GLTFBufferView> bufferViews;
  /**
   * An array of cameras.  A camera defines a projection matrix.
   */
  @JsonProperty("cameras")
  @Valid
  private List<GLTFCamera> cameras;
  /**
   * An array of images.  An image defines data used to create a texture.
   */
  @JsonProperty("images")
  @Valid
  private List<GLTFImage> images;
  /**
   * An array of materials. A material defines the appearance of a primitive.
   */
  @JsonProperty("materials")
  @Valid
  private List<GLTFMaterial> materials;
  /**
   * An array of meshes. A mes is a set of primitives to be rendered.
   */
  @JsonProperty("meshes")
  @Valid
  private List<GLTFMesh> meshes;
  /**
   * An array of nodes.
   */
  @JsonProperty("nodes")
  @Valid
  private List<GLTFNode> nodes;
  /**
   * An array of samplers.  A sampler contains properties for texture filtering and wrapping modes.
   */
  @JsonProperty("samplers")
  @Valid
  private List<GLTFSampler> samplers;
  /**
   * The index of the default scene.
   */
  private GLTFScene defaultScene;

  /**
   * An array of scenes.
   */
  @JsonProperty("scenes")
  @Valid
  private List<GLTFScene> scenes;
  /**
   * An array of skins. A skin is defined by joints and matrices.
   */
  @JsonProperty("skins")
  @Valid
  private List<GLTFSkin> skins;
  /**
   * An array of textures. minItems 1
   */
  @JsonProperty("textures")
  @Valid
  private List<GLTFTexture> textures;
  /**
   * The URI of the root file for this GLTF asset Used to resolve non-absolute file paths
   */
  private URI source;

  /**
   * The implementation of how to convert from a URI to a Stream Used to load all files
   */
  private GLTFImporter gltfImporter;

  public GLTF(GLTFImporter gltfImporter, URI source) {
    this.gltfImporter = gltfImporter;
    this.source = source;
  }

  GLTFImporter getGltfImporter() {
    return gltfImporter;
  }

  /**
   * Resolve relativePath against the base URI for this file
   *
   * @param relativePath
   * @return
   */
  URI resolveURI(String relativePath) {
    URI resolved = source.resolve(relativePath);
    logger.info("GLTF debug resolve relativePath: " + resolved);
    return resolved;
  }

  @JsonProperty("scene")
  private void setDefaultScene(int index) {
    gltf.indexResolvers.add(() -> defaultScene = gltf.getScene(index));
  }

  /**
   * @return the default Scene, or null if undefined
   */
  public Optional<GLTFScene> getDefaultScene() {
    return Optional.ofNullable(defaultScene);
  }

  public List<GLTFScene> getScenes() {
    return Collections.unmodifiableList(scenes);
  }

  GLTFBufferView getBufferView(int indexBufferView) {
    return this.bufferViews.get(indexBufferView);
  }

  GLTFBuffer getBuffer(int indexBuffer) {
    return this.buffers.get(indexBuffer);
  }

  GLTFMaterial getMaterial(int indexMaterial) {
    return this.materials.get(indexMaterial);
  }

  GLTFAccessor getAccessor(int indexAccessor) {
    return this.accessors.get(indexAccessor);
  }

  GLTFNode getNode(int indexNode) {
    return this.nodes.get(indexNode);
  }

  GLTFCamera getCamera(int indexCamera) {
    return this.cameras.get(indexCamera);
  }

  GLTFSkin getSkin(int indexSkin) {
    return this.skins.get(indexSkin);
  }

  GLTFMesh getMesh(int indexMesh) {
    return this.meshes.get(indexMesh);
  }

  GLTFTexture getTexture(int indexTexture) {
    return this.textures.get(indexTexture);
  }

  GLTFImage getImage(int indexImage) {
    return this.images.get(indexImage);
  }

  GLTFSampler getSampler(int indexSampler) {
    return samplers.get(indexSampler);
  }

  private GLTFScene getScene(int index) {
    return scenes.get(index);
  }

  public LinkedHashSet<String> getExtensionsUsed() {
    return extensionsUsed;
  }

  public LinkedHashSet<String> getExtensionsRequired() {
    return extensionsRequired;
  }

  public List<GLTFAnimation> getAnimations() {
    return animations;
  }

  public void applyLookupMap() {
    indexResolvers.forEach(Runnable::run);
  }
}
