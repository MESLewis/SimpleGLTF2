/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFMeshPrimitive;
import com.meslewis.simplegltf2.data.GLTFNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.AABBf;
import org.joml.Vector3f;

/**
 * Container object for a GLTFMeshPrimitive and some metadata used by renderer
 * KhronosGroup/glTF-Sample-Viewer puts this data in GLTFMeshPrimitive directly
 */
public class RenderObject extends RenderNode {

  /**
   * Very similar to GLTFMeshPrimitive.getAttributes but the string is a variable in the shader
   * code
   */
  private Map<String, GLTFAccessor> glAttributes = new HashMap<>();
  private List<String> defines = new ArrayList<>();
  private boolean skip = true; //Spec defines if position does not exist then skip
  private boolean hasWeights = false;
  private boolean hasJoints = false;
  private AABBf boundingBox;

  private GLTFMeshPrimitive primitive;
  private RenderMaterial material;

  RenderObject(GLTFMeshPrimitive primitive, GLTFNode node, RenderNode parentNode) {
    super(node, parentNode);
    this.primitive = primitive;

    primitive.getMaterial()
        .ifPresent(gltfMaterial -> this.material = new RenderMaterial(gltfMaterial));

    for (String key : primitive.getAttributes().keySet()) {
      //TODO error checking for max vertex attribs

      GLTFAccessor accessor = primitive.getAttributes().get(key);
      switch (key) {
        case "POSITION":
          this.skip = false;
          glAttributes.put("a_Position", accessor);
          break;
        case "NORMAL":
          defines.add("HAS_NORMALS 1");
          glAttributes.put("a_Normal", accessor);
          break;
        case "TANGENT":
          defines.add("HAS_TANGENTS 1");
          glAttributes.put("a_Tangent", accessor);
          break;
        case "TEXCOORD_0":
          defines.add("HAS_UV_SET1 1");
          glAttributes.put("a_UV1", accessor);
          break;
        case "TEXCOORD_1":
          defines.add("HAS_UV_SET2 1");
          glAttributes.put("a_UV2", accessor);
          break;
        case "COLOR_0":
          //e.g. VEC3 , VEC4
          defines.add("HAS_VERTEX_COLOR_" + accessor.getDataType().name() + " 1");
          glAttributes.put("a_Color", accessor);
          break;
        case "JOINTS_0":
          hasJoints = true;
          defines.add("HAS_JOINT_SET1 1");
          glAttributes.put("a_Joint1", accessor);
          break;
        case "WEIGHTS_0":
          hasWeights = true;
          defines.add("HAS_WEIGHT_SET1 1");
          glAttributes.put("a_Weight1", accessor);
          break;
        case "JOINTS_1":
          hasJoints = true;
          defines.add("HAS_JOINT_SET2 1");
          glAttributes.put("a_Joint2", accessor);
          break;
        case "WEIGHTS_1":
          hasWeights = true;
          defines.add("HAS_WEIGHT_SET2 1");
          glAttributes.put("a_Weight2", accessor);
          break;
        default:
          System.err.println("Unknown attribute: " + key);
      }
    }
    //TODO morph targets
  }

  public Map<String, GLTFAccessor> getGlAttributes() {
    return glAttributes;
  }

  public List<String> getDefines() {
    return defines;
  }

  public String getShaderIdentifier() {
    return "primitive.vert";
  }

  public boolean isSkip() {
    return skip;
  }

  public boolean isHasWeights() {
    return hasWeights;
  }

  public boolean isHasJoints() {
    return hasJoints;
  }

  public GLTFMeshPrimitive getPrimitive() {
    return primitive;
  }

  public RenderMaterial getMaterial() {
    return material;
  }

  /**
   * Get the axis aligned bounding box for this node TODO including children?
   *
   * @return
   */
  public AABBf getBoundingBox() {
    if (boundingBox != null) {
      return boundingBox;
    }

    this.boundingBox = new AABBf();
    GLTFAccessor accessor = this.getPrimitive().getAttributes().get("POSITION");

    if (accessor == null) {
      return boundingBox;
    }

    ArrayList<Integer> maxList = accessor.getMax();
    Vector3f max = new Vector3f(maxList.get(0), maxList.get(1), maxList.get(2));

    ArrayList<Integer> minList = accessor.getMin();
    Vector3f min = new Vector3f(minList.get(0), minList.get(1), minList.get(2));

    boundingBox.union(max).union(min);

    return boundingBox;
  }
}
