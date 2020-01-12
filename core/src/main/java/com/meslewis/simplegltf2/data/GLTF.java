/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.meslewis.simplegltf2.StreamIO;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
  @JsonProperty("animations")
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
   * An array of textures. minItems 1
   */
  @JsonProperty("textures")
  private ArrayList<GLTFTexture> textures;
  /**
   * The URI of the root file for this GLTF asset Used to resolve non-absolute file paths
   */
  private URI source;

  /**
   * The implementation of how to convert from a URI to a Stream Used to load all files
   */
  private StreamIO streamIO;

  public GLTF(StreamIO streamIO, URI source) {
    this.streamIO = streamIO;
    this.source = source;
  }

  /**
   * Small helper function for data classes to access the StreamIO
   *
   * @param uri
   * @return
   */
  InputStream getInputStream(URI uri) {
    return this.streamIO.getStreamForResource(uri);
  }

  /**
   * Resolve relativePath against the base URI for this file
   *
   * @param relativePath
   * @return
   */
  URI resolveURI(String relativePath) {
    URI resolved = source.resolve(relativePath);
    System.out.println("GLTF debug resolve relativePath: " + resolved);
    return resolved;
  }

  /**
   * @return the default Scene, or null if undefined
   */
  public GLTFScene getDefaultScene() {
    if(indexDefaultScene == null) {
      return null;
    }
    return scenes.get(indexDefaultScene);
  }

  public List<GLTFScene> getScenes() {
    return Collections.unmodifiableList(scenes);
  }

  GLTFBufferView getBufferView(Integer indexBufferView) {
    return this.bufferViews.get(indexBufferView);
  }

  GLTFBuffer getBuffer(Integer indexBuffer) {
    return this.buffers.get(indexBuffer);
  }

  GLTFMaterial getMaterial(Integer indexMaterial) {
    return this.materials.get(indexMaterial);
  }

  GLTFAccessor getAccessor(Integer indexAccessor) {
    return this.accessors.get(indexAccessor);
  }

  GLTFNode getNode(Integer indexNode) {
    return this.nodes.get(indexNode);
  }

  GLTFCamera getCamera(Integer indexCamera) {
    return this.cameras.get(indexCamera);
  }

  GLTFSkin getSkin(Integer indexSkin) {
    return this.skins.get(indexSkin);
  }

  Optional<GLTFMesh> getMesh(Integer indexMesh) {
    if (indexMesh != null) {
      return Optional.of(this.meshes.get(indexMesh));
    } else {
      return Optional.empty();
    }
  }

  GLTFTexture getTexture(Integer indexTexture) {
    return this.textures.get(indexTexture);
  }

  GLTFImage getImage(Integer indexImage) {
    return this.images.get(indexImage);
  }
}
