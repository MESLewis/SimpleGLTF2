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
import java.util.Map.Entry;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Renderer {

  private Vector3f cameraPos = new Vector3f().zero();


  public void draw(Matrix4f viewProjectionMatrix, ArrayList<RenderObject> renderObjects) {
    for (RenderObject renderObject : renderObjects) {
      if (renderObject.isSkip()) {
        continue;
      }
      //select shader permutation, compile and link program.
      ArrayList<String> vertDefines = new ArrayList<>();
      //TODO skinning and morphin need some extra
      vertDefines.addAll(renderObject.getDefines());

      RenderMaterial material = renderObject.getMaterial();
      ArrayList<String> fragDefines = new ArrayList<>();
      //TODO skinning and morphing need some extra defines
      //TODO material has some frag defines
      fragDefines.addAll(material.getDefines());

      int fragmentHash = ShaderCache.selectShader("metallic-roughness.frag", fragDefines);
      int vertexHash = ShaderCache.selectShader(renderObject.getShaderIdentifier(), vertDefines);

      ShaderProgram shader = ShaderCache.getShaderProgram(fragmentHash, vertexHash);

      glUseProgram(shader.getProgramId());

      shader.setUniform("u_ViewProjectionMatrix", viewProjectionMatrix);
//      shader.setUniform("u_ModelMatrix", )
//      shader.setUniform("u_NormalMatrix",)
//      shader.setUniform("u_Exposure", )
      shader.setUniform("u_Camera", cameraPos);

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

      //TODO material properties

      //TODO textures

      if (drawIndexed) {
        GLTFAccessor indexAccessor = renderObject.getPrimitive().getIndicesAccessor();
        //TODO GL_TRIANGLES should not hard coded
        glDrawElements(GL_TRIANGLES, indexAccessor.getElementCount(), indexAccessor.getGLType(), 0);
      } else {
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
      }

      for (String attribute : renderObject.getGlAttributes().keySet()) {
        glDisableVertexAttribArray(shader.getAttributeLocation(attribute));
      }
    }
  }
}
