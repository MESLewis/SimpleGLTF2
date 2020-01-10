/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.validation.constraints.Min;

public class GLTFMeshPrimitive extends GLTFProperty {

  /**
   * A dictionary object, where each key corresponds to mesh attribute semantic and each value is
   * the index of the accessor containing attribute's data.
   */
  @JsonProperty("attributes")
  private Map<GLTFAttributeType, Integer> attributes;
  /**
   * The type of primitives to render. All valid values correspond to WebGL enums.
   * <p>
   * default - 4 = TRIANGLES
   */
  @JsonProperty("mode")
  private GLTFMode mode = GLTFMode.TRIANGLES;
  /**
   * The index of the accessor that contains mesh indices. When this is not defined, the primitives
   * should be rendered without indices using `drawArrays()`. When defined, the accessor must
   * contain indices: the `bufferView` referenced by the accessor should have a `target` equal to
   * 34963 (ELEMENT_ARRAY_BUFFER); `componentType` must be 5121 (UNSIGNED_BYTE), 5123
   * (UNSIGNED_SHORT) or 5125 (UNSIGNED_INT), the latter may require enabling additional hardware
   * support; `type` must be `\ TODO handle not being defined
   */
  @JsonProperty("indices")
  @Min(0)
  private Integer indexIndicesAccessor;
  /**
   * The index of the material to apply to this primitive when rendering.
   */
  @JsonProperty("material")
  @Min(0)
  private Integer indexMaterial;

  /**
   * A dictionary object specifying attributes displacements in a Morph Target, where each key
   * corresponds to one of the three supported attribute semantic (`POSITION`, `NORMAL`, or
   * `TANGENT`) and each value is the index of the accessor containing the attribute displacements'
   * data.
   */
  @JsonSetter("targets")
  private ArrayList<Map<GLTFAttributeType, Integer>> morphTargets;

  /**
   * Setter to handle custom attributes
   */
  @JsonSetter("attributes")
  private void setAttributes(String name, Integer index) {
    if (attributes == null) {
      attributes = new LinkedHashMap<>();
    }

    //Custom attributes
    if (name.startsWith("_")) {
      throw new RuntimeException("Custom attributes are not currently supported by SimpleGLTF2");
    } else {
      attributes.put(GLTFAttributeType.valueOf(name), index);
    }
  }

  /**
   * Get a Map of references to Accessors for the AdditionalProperties of this MeshPrimitive
   *
   * @return null if attributes is null
   */
  public Map<GLTFAttributeType, GLTFAccessor> getAttributes() {
    if (attributes == null) {
      return null;
    }

    Map<GLTFAttributeType, GLTFAccessor> accessorMap = new LinkedHashMap<>();

    for (Map.Entry<GLTFAttributeType, Integer> entry : attributes.entrySet()) {
      accessorMap.put(entry.getKey(), gltf.getAccessor(entry.getValue()));
    }

    return accessorMap;
  }

  /**
   * Get a reference to the Accessor for this MeshPrimitive
   */
  public GLTFAccessor getIndicesAccessor() {
    return gltf.getAccessor(indexIndicesAccessor);
  }

  /**
   * Get a reference to Material for this MeshPrimitive
   */
  public GLTFMaterial getMaterial() {
    return gltf.getMaterial(indexMaterial);
  }

  @JsonSetter("mode")
  private void setMode(int mode) {
    this.mode = GLTFMode.values()[mode];
  }

  /**
   * Get the Mode for this MeshPrimitive
   */
  public GLTFMode getMode() {
    return this.mode;
  }
}
