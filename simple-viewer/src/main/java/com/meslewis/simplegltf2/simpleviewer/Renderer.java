/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import com.meslewis.simplegltf2.data.GLTFAccessorPrimitiveType;
import com.meslewis.simplegltf2.simpleviewer.SimpleViewer.GLTFRenderObject;
import java.util.ArrayList;
import org.joml.Matrix4f;

public class Renderer {


  public void draw(ShaderProgram shaderProgram, Matrix4f modelViewProjectionMatrix,
      ArrayList<GLTFRenderObject> gltfRenderObjects) {
    glUseProgram(shaderProgram.getProgramId());

    float[] mvpm = new float[16];
    modelViewProjectionMatrix.get(mvpm);

    glUniformMatrix4fv(shaderProgram.getUniform("u_ModelViewProjection"), false, mvpm);

    for (int i = 0; i < gltfRenderObjects.size(); ++i) {
      GLTFRenderObject renderObject = gltfRenderObjects.get(i);

      glBindBuffer(GL_ARRAY_BUFFER, renderObject.vertexBufferId);

      int verticesBaseAddress = 0;
      int positionAttribute = shaderProgram.getAttribute("a_Position");
      glVertexAttribPointer(positionAttribute, 3, GL_FLOAT, false, 0, verticesBaseAddress);

//      glBindBuffer(GL_ARRAY_BUFFER, 0);

      glEnableVertexAttribArray(positionAttribute);

      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderObject.indexBufferId);
      int numElements =
          renderObject.indexByteLength / GLTFAccessorPrimitiveType.SHORT.getSizeInBytes();
      glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
//      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

      glDisableVertexAttribArray(positionAttribute);
    }
    glUseProgram(0);
  }

}
