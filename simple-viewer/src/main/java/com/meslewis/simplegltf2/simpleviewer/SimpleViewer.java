/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.meslewis.simplegltf2.GLTFImporter;
import com.meslewis.simplegltf2.data.GLTF;
import com.meslewis.simplegltf2.data.GLTFAccessor;
import com.meslewis.simplegltf2.data.GLTFAccessorPrimitiveType;
import com.meslewis.simplegltf2.data.GLTFMesh;
import com.meslewis.simplegltf2.data.GLTFMeshPrimitive;
import com.meslewis.simplegltf2.data.GLTFNode;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

//Initial configuration from https://www.lwjgl.org/guide
public class SimpleViewer {

  private static final int WIDTH = 700;
  private static final int HEIGHT = 500;

  private static final float FOVY = 70f;
  private static final float Z_NEAR = 1f;
  private static final float Z_FAR = 1000f;

  private GLTFImporter gltfImporter;
  private ArrayList<GLTFRenderObject> gltfRenderObjects = new ArrayList<>();
  private ShaderProgram shaderProgram;

  private final Matrix4f modelMatrix = new Matrix4f().identity();
  private final Matrix4f modelViewMatrix = new Matrix4f().identity();
  private final Matrix4f modelViewProjectionMatrix = new Matrix4f().identity();


  private final Matrix4f viewMatrix = new Matrix4f().identity();
  private final Matrix4f projectionMatrix = new Matrix4f().identity();

  private float aspectRatio = ((float) WIDTH) / HEIGHT;

  // The window handle
  private long window;

  //Default file to load
//  private String defaultFilePath = "/simplest.gltf";
  private String defaultFilePath = "/default/chicken/Chicken.gltf";

  public static class GLTFRenderObject {

    ShortBuffer indices;
    int indexByteOffset;
    int indexByteLength;
    int indexBufferId;

    FloatBuffer vertices;
    int vertexByteOffset;
    int vertexByteLength;
    int vertexBufferId;
  }

  public void run() {
    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  private void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW"); }

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, "Simple GLTF2 Viewer", NULL, NULL);
    if (window == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
    });

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(window, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center the window
      glfwSetWindowPos(
          window,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2
      );
    } // the stack frame is popped automatically

    // Make the window visible
    glfwShowWindow(window);

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    gltfImporter = new GLTFImporter();

    projectionMatrix.perspective(FOVY, aspectRatio, Z_NEAR, Z_FAR);
    viewMatrix.identity();
    modelMatrix.identity();

  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // Set the clear color
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

    shaderProgram = new ShaderProgram("/default/shaders/simplefrag.frag",
        "/default/shaders/simplevert.vert");

    glUseProgram(shaderProgram.getProgramId());
    loadFile(getResourceAbsolutePath() + defaultFilePath);
    glUseProgram(0);

    Renderer renderer = new Renderer();

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

      //glTF-Sample-Viewer/viewer.js:render()

      //drawScene
      //TODO viewProjectionMatrix

//      nodeList.stream()
//          .map(GLTFNode::getMesh)
//          .filter(Optional::isPresent)
//          .map(Optional::get)
//          .flatMap(gltfMesh -> gltfMesh.getPrimitives().stream())
//          .forEach(meshPrimitive -> this.drawPrimitive(meshPrimitive));

      //TODO eyeZ was -1 but that made it flip around like crazy, 1 works for now but is probably backwards or something
      viewMatrix.setLookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);
      updateModelMatrix(modelMatrix, 0.1f * aspectRatio);
      viewMatrix.mul(modelMatrix, modelViewMatrix);
      projectionMatrix.mul(modelViewMatrix, modelViewProjectionMatrix);

      renderer.draw(shaderProgram, modelViewProjectionMatrix, gltfRenderObjects);

      glfwSwapBuffers(window); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }
  }


  private void loadFile(String path) {
    GLTF gltf;
    try {
      URI uri = new File(path).toURI();
      gltf = gltfImporter.load(uri);
    } catch (IOException e) {
      //TODO alert failed load
      e.printStackTrace();
      return;
    }

    try {
      shaderProgram.createUniform("u_ModelViewProjection");
      shaderProgram.createAttribute("a_Position");
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<GLTFNode> fullNodeList = gltf.getDefaultScene()
        .orElseGet(() -> gltf.getScenes().get(0))
        .getAllNodesAndDescendants();

    fullNodeList.stream()
        .map(GLTFNode::getMesh)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(this::processGLTFMesh);
  }

  private void processGLTFMesh(GLTFMesh mesh) {
    for (GLTFMeshPrimitive primitive : mesh.getPrimitives()) {
      System.out.println("Processing glTF mesh");
      //Each primitive gets its own render object
      GLTFRenderObject renderObject = new GLTFRenderObject();

      GLTFAccessor positionAccessor = primitive.getAttributes().get("POSITION");

      //Hard coded vertex data
      if (positionAccessor.getPrimitiveType() == GLTFAccessorPrimitiveType.FLOAT) {
        renderObject.vertices = ByteBuffer.allocateDirect(positionAccessor.getSizeInBytes())
            .order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        renderObject.vertices
            .put(positionAccessor.getData().order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer());
        renderObject.vertexByteLength = positionAccessor.getSizeInBytes();
        renderObject.vertexByteOffset = positionAccessor.getByteOffset();
        renderObject.vertices.position(
            positionAccessor.getByteOffset() / positionAccessor.getPrimitiveType()
                .getSizeInBytes());
      } else {
        throw new RuntimeException("Not implemented");
      }

      //Hard coded index data
      GLTFAccessor indicesAccessor = primitive.getIndicesAccessor();

      //Removed this to draw the chicken
//      if (indicesAccessor.getTarget() == GLTFBufferViewTarget.ELEMENT_ARRAY_BUFFER) {
      renderObject.indices = ByteBuffer.allocateDirect(indicesAccessor.getSizeInBytes())
          .order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
      ShortBuffer debug = indicesAccessor.getData().order(ByteOrder.LITTLE_ENDIAN)
          .asShortBuffer();
      renderObject.indices.put(indicesAccessor.getData().asShortBuffer());
      renderObject.indexByteLength = indicesAccessor.getSizeInBytes();
      renderObject.indexByteOffset = indicesAccessor.getByteOffset();
      renderObject.indices.position(
          indicesAccessor.getByteOffset() / indicesAccessor.getPrimitiveType().getSizeInBytes());
//      } else {
//        throw new RuntimeException("Not implemented");
//      }

      //Prepare and upload GPU data
      int[] buffers = new int[2];

      glGenBuffers(buffers);
      renderObject.vertexBufferId = buffers[0];
      renderObject.indexBufferId = buffers[1];

      System.out.println(ByteOrder.nativeOrder());

      //Upload vertex to gpu
      renderObject.vertices.position(
          renderObject.vertexByteOffset / GLTFAccessorPrimitiveType.FLOAT.getSizeInBytes());
      glBindBuffer(GL_ARRAY_BUFFER, renderObject.vertexBufferId);
      glBufferData(GL_ARRAY_BUFFER, renderObject.vertices, GL_STATIC_DRAW);
//      glBindBuffer(GL_ARRAY_BUFFER, 0);

      //Upload index to gpu
      renderObject.indices.position(
          renderObject.indexByteOffset / GLTFAccessorPrimitiveType.SHORT.getSizeInBytes());
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, renderObject.indexBufferId);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, renderObject.indices, GL_STATIC_DRAW);
//      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

      gltfRenderObjects.add(renderObject);
    }
  }

  private void updateModelMatrix(Matrix4f modelMatrix, float scaleFactor) {
    this.modelMatrix.identity().scale(scaleFactor);
  }

  private static final Map<GLTFAccessor, Integer> accessorGlBufferMap = new HashMap<>();

  private void processMeshAttribute(String name, GLTFAccessor accessor) {
    //TODO move stuff from processGLTFMesh to here
//    if (!accessorGlBufferMap.containsKey(accessor)) {
//      accessorGlBufferMap.put(accessor, glGenBuffers());
//      glBindBuffer(GL_ARRAY_BUFFER, accessorGlBufferMap.get(accessor));
//      glBufferData(GL_ARRAY_BUFFER, accessor.getData(), GL_STATIC_DRAW);
//    } else {
//      glBindBuffer(GL_ARRAY_BUFFER, accessorGlBufferMap.get(accessor));
//    }
  }

  private static String getResourceAbsolutePath() {
    return new File("src/main/resources").getAbsolutePath();
  }

  public static void main(String[] args) {
    new SimpleViewer().run();
  }
}
