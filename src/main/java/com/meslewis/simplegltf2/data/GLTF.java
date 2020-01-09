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
import javax.validation.constraints.NotNull;

<<<<<<<HEAD
/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

=======
    >>>>>>>8417a3b...Squash bad history
    package com.meslewis.simplegltf2.data;
    <<<<<<<HEAD
=======
>>>>>>>8417a3b...Squash bad history

/**
 * The root object fr a glTF asset
 */
public class GLTF extends GLTFProperty {

<<<<<<<HEAD
  @JsonAnySetter
  private final Map<String, JsonNode> extraValues = new HashMap<>();
=======
  //TODO check for this being empty, if not empty something is wrong
  @JsonAnySetter
  Map<String, JsonNode> extraValues = new HashMap<>();
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * Names of glTF extensions used somewhere in this asset.
   */
  @JsonProperty("extensionsUsed")
<<<<<<<HEAD
  private LinkedHashSet<String> extensionsUsed;
  /**
   * Names of glTF extensions required to properly load this asset.
   */
  @JsonProperty("extensionsRequired")
  private LinkedHashSet<String> extensionsRequired;
=======
  LinkedHashSet<String> extensionsUsed;
  /**
   * Names of glTF extensions required to properly load this asset.
   */
  @JsonProperty("extensionsRequred")
  LinkedHashSet<String> extensionsRequired;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of accessors. An accessor is a typed view into a bufferView
   */
  @JsonProperty("accessors")
<<<<<<<HEAD
  private ArrayList<GLTFAccessor> accessors;
  /**
   * An array of keyframe animations.
   */
  @JsonProperty("animation")
  private ArrayList<GLTFAnimation> animations;
=======
  ArrayList<GLTFAccessor> accessors;
  /**
   * An array of keyframe animations. TODO minItems 1
   */
  @JsonProperty("animation")
  ArrayList<GLTFAnimation> animations;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * Metadata about the glTF asset.
   */
  @JsonProperty("asset")
  @NotNull
<<<<<<<HEAD
  private GLTFAsset asset;
=======
  GLTFAsset asset;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of buffers. A buffer points to binary geometry, animation, or kins.
   */
  @JsonProperty("buffers")
<<<<<<<HEAD
  private ArrayList<GLTFBuffer> buffers;
=======
  ArrayList<GLTFBuffer> buffers;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of bufferViews.  A bufferView is a view into a buffer generally representing a subset
   * of the buffer.
   */
  @JsonProperty("bufferViews")
<<<<<<<HEAD
  private ArrayList<GLTFBufferView> bufferViews;
=======
  ArrayList<GLTFBufferView> bufferViews;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of cameras.  A camera defines a projection matrix.
   */
  @JsonProperty("cameras")
<<<<<<<HEAD
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
=======
  ArrayList<GLTFCamera> cameras;
  /**
   * An array of images.  An image defines data used to create a texture. TODO minItems 1
   */
  @JsonProperty("images")
  ArrayList<GLTFImage> images;
  /**
   * An array of materials. A material defines the appearance of a primitive. TODO minItems 1
   */
  @JsonProperty("materials")
  ArrayList<GLTFMaterial> materials;
  /**
   * An array of meshes. A mes is a set of primitives to be rendered. TODO minItems 1
   */
  @JsonProperty("meshes")
  ArrayList<GLTFMesh> meshes;
  /**
   * An array of nodes.
   * <p>
   * TODO minItems 1
   */
  @JsonProperty("nodes")
  ArrayList<GLTFNode> nodes;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of samplers.  A sampler contains properties for texture filtering and wrapping modes.
   */
  @JsonProperty("samplers")
<<<<<<<HEAD
  private ArrayList<GLTFSampler> samplers;
=======
  ArrayList<GLTFSampler> samplers;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * The index of the default scene.
   */
  @JsonProperty("scene")
<<<<<<<HEAD
  private Integer indexDefaultScene;
=======
  Integer scene;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of scenes.
   */
  @JsonProperty("scenes")
<<<<<<<HEAD
  private ArrayList<GLTFScene> scenes;
=======
  ArrayList<GLTFScene> scenes;
>>>>>>>8417a3b...
  Squash bad
  history
  /**
   * An array of skins. A skin is defined by joints and matrices.
   */
  @JsonProperty("skins")
  ArrayList<GLTFSkin> skins;
  /**
   * An array of textures. minItems 1
   */
  @JsonProperty("textures")
  ArrayList<GLTFTexture> textures;
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
    if (scene == null) {
      return null;
    }
    return scenes.get(scene);
  }

  public List<GLTFScene> getScenes() {
    return Collections.unmodifiableList(scenes);
  }

  public List<GLTFMesh> getMeshes() {
    return Collections.unmodifiableList(meshes);
  }
}
