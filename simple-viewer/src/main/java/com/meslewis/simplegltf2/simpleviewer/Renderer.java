/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUseProgram;

import com.meslewis.simplegltf2.data.GLTFAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.joml.Matrix4f;

public class Renderer {

  private List<RenderLight> visibleLights;

  private Matrix4f projMatrix;
  private Matrix4f viewMatrix;
  private Matrix4f viewProjectionMatrix = new Matrix4f();

  public void draw(RenderCamera camera, ArrayList<RenderObject> renderObjects) {
    visibleLights = new ArrayList<>();
    visibleLights.add(new RenderLight(null, null));

    for (RenderObject renderObject : renderObjects) {
      if (renderObject.isSkip()) {
        continue;
      }
      //select shader permutation, compile and link program.
      ArrayList<String> vertDefines = new ArrayList<>();
      //TODO skinning and morphin need some extra
      vertDefines.addAll(renderObject.getDefines());

      RenderMaterial material = renderObject.getMaterial();
      //TODO how to handle null material? I think a default material is defined.
      if (material == null) {
        material = RenderMaterial.defaultMaterial;
      }

      ArrayList<String> fragDefines = new ArrayList<>();
      //TODO skinning and morphing need some extra defines
      fragDefines.addAll(material.getDefines());
      fragDefines.add("USE_PUNCTUAL 1");
      fragDefines.add("LIGHT_COUNT " + visibleLights.size());

      int vertexHash = ShaderCache.selectShader(renderObject.getShaderIdentifier(), vertDefines);
      int fragmentHash = ShaderCache.selectShader(material.getShaderIdentifier(), fragDefines);

      ShaderProgram shader = ShaderCache.getShaderProgram(vertexHash, fragmentHash);

      glUseProgram(shader.getProgramId());

      //applyLights
      List<UniformLight> uniformLights = new ArrayList<>();
      for (RenderLight light : visibleLights) {
        uniformLights.add(light.toUniform());
      }
      shader.setUniform("u_Lights", uniformLights);

      camera.updatePosition();

      this.projMatrix = camera.getProjectionMatrix();
      this.viewMatrix = camera.getViewMatrix();
      projMatrix.mul(viewMatrix, viewProjectionMatrix);

      shader.setUniform("u_ViewProjectionMatrix", viewProjectionMatrix);
      shader.setUniform("u_ModelMatrix", renderObject.getWorldTransform());
      shader.setUniform("u_NormalMatrix", renderObject.getNormalMatrix());
      shader.setUniform("u_Exposure", 0.5f); //TODO
      shader.setUniform("u_Camera", camera.getPosition());

      boolean drawIndexed = renderObject.getPrimitive().getIndicesAccessor() != null;

      if (drawIndexed) {
        GlUtil.setIndices(renderObject.getPrimitive().getIndicesAccessor());
      }

      int vertexCount = 0;
      for (Entry<String, GLTFAccessor> entry : renderObject.getGlAttributes().entrySet()) {
        String attributeName = entry.getKey();
        GLTFAccessor accessor = entry.getValue();

        vertexCount = accessor.getElementCount();

        int location = shader.getAttributeLocation(attributeName);

        if (location < 0) {
          continue;
        }
        GlUtil.enableAttribute(location, accessor);
      }

      for (Entry<String, Object> entry : material.getProperties().entrySet()) {
        shader.setUniform(entry.getKey(), entry.getValue());
      }

      //TODO textures
      for (Entry<String, RenderTexture> entry : material.getTexturesMap()
          .entrySet()) {
        RenderTexture info = entry.getValue();
        int location = shader.getUniformLocation(entry.getKey());

        if (location < 0) {
          continue;
        }
        GlUtil.setTexture(location, info);
      }

      if (drawIndexed) {
        GLTFAccessor indexAccessor = renderObject.getPrimitive().getIndicesAccessor();
        //TODO GL_TRIANGLES should not hard coded
        glDrawElements(GL_TRIANGLES, indexAccessor.getElementCount(), indexAccessor.getGLType(), 0);
      } else {
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
      }

      for (String attribute : renderObject.getGlAttributes().keySet()) {
        int location = shader.getAttributeLocation(attribute);
        if (location < 0) {
          continue;
        }
        glDisableVertexAttribArray(location);
      }
    }
  }
}
