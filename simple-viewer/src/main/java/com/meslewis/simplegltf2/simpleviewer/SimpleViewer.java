/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
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
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.meslewis.simplegltf2.GLTFImporter;
import com.meslewis.simplegltf2.data.GLTF;
import com.meslewis.simplegltf2.data.GLTFCamera;
import com.meslewis.simplegltf2.data.GLTFMesh;
import com.meslewis.simplegltf2.data.GLTFMeshPrimitive;
import com.meslewis.simplegltf2.data.GLTFNode;
import com.meslewis.simplegltf2.data.GLTFScene;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Initial configuration from https://www.lwjgl.org/guide
public class SimpleViewer {

  private static final Logger logger = LoggerFactory.getLogger(SimpleViewer.class);

  private static final int WIDTH = 700;
  private static final int HEIGHT = 500;

  private static final float FOVY = 70f;
  private static final float Z_NEAR = 1f;
  private static final float Z_FAR = 1000f;

  private GLTFImporter gltfImporter;
  private ArrayList<RenderObject> renderObjects = new ArrayList<>();

  private final Matrix4f modelMatrix = new Matrix4f().identity();
  private final Matrix4f modelViewMatrix = new Matrix4f().identity();


  private final Matrix4f viewMatrix = new Matrix4f().identity();
  private final Matrix4f projectionMatrix = new Matrix4f().identity();
  private final Matrix4f viewProjectionMatrix = new Matrix4f().identity();

  private float aspectRatio = ((float) WIDTH) / HEIGHT;

  private List<File> testFileList;
  private int nextTestFileIndex = 0;

  // The window handle
  private long window;

  //Default file to load
//  private String defaultFilePath = "/simplest.gltf";
  private String defaultFilePath = "/default/chicken/Chicken.gltf";

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

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, "Simple GLTF2 Viewer", NULL, NULL);
    if (window == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
      if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
        loadNextFile();
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

    //Set up file list from tests
    Path parentProject = Paths.get(new File(".").toURI()).getParent().getParent().toAbsolutePath();
    URI test = parentProject.toUri().resolve("core/src/test/resources/glTF-Sample-Models/");

    File modelsRoot = new File(test);

    ArrayList<File> fileList = new ArrayList<>();

    getAllFileChildren(modelsRoot, fileList);

    testFileList = fileList.stream()
        .filter(file -> file.getName().endsWith(".gltf"))
        .collect(Collectors.toList());
  }

  private void loadNextFile() {
    File next = testFileList.get(nextTestFileIndex++);
    logger.info("Loading new model: " + next.getName());
    loadFile(next.getAbsolutePath());
  }

  private void getAllFileChildren(File file, List<File> retList) {
    if (file != null) {
      retList.add(file);
      if (file.isDirectory()) {
        File[] files = file.listFiles();
        if (files != null) {
          Arrays.stream(files).filter(Objects::nonNull)
              .forEach(file1 -> getAllFileChildren(file1, retList));
        }
      }
    }
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    GLUtil.setupDebugMessageCallback();

    //Need a default vertex array
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

    // Set the clear color
    glClearColor(0.0f, 1.0f, 1.0f, 0.0f);

    //TODO load next file
//    loadFile(getResourceAbsolutePath() + defaultFilePath);
    nextTestFileIndex = 14;
    loadNextFile();

    Renderer renderer = new Renderer();

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

      //TODO eyeZ was -1 but that made it flip around like crazy, 1 works for now but is probably backwards or something
      viewMatrix.setLookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);
      updateModelMatrix(modelMatrix, 0.001f * aspectRatio);
      viewMatrix.mul(modelMatrix, modelViewMatrix);
      projectionMatrix.mul(viewMatrix, viewProjectionMatrix);

      renderer.draw(viewProjectionMatrix, renderObjects);

      glfwSwapBuffers(window); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }
  }


  private void loadFile(String path) {
    //Clear before loading
    renderObjects.clear();

    GLTF gltf;
    try {
      URI uri = new File(path).toURI();
      gltf = gltfImporter.load(uri);
    } catch (IOException e) {
      //TODO alert failed load
      e.printStackTrace();
      return;
    }

    if (gltf.getExtensionsRequired() != null) {
      throw new RuntimeException("Extensions not supported");
    }

    GLTFScene scene = gltf.getDefaultScene().orElseGet(() -> gltf.getScenes().get(0));

    for (GLTFNode rootNode : scene.getRootNodes()) {
      processNodeChildren(rootNode, null);
    }
  }

  private void processNodeChildren(GLTFNode node, RenderNode parent) {
    RenderNode renderNode;
    Optional<GLTFMesh> mesh = node.getMesh();
    if (mesh.isPresent()) {
      GLTFMesh gltfMesh = mesh.get();
      renderNode = new RenderNode(node, parent);
      for (GLTFMeshPrimitive primitive : gltfMesh.getPrimitives()) {
        logger.debug("Processing GLTFMesh " + gltfMesh.getName());
        //Each primitive gets its own render object
        RenderObject renderObject = new RenderObject(primitive, node, renderNode);
        renderObjects.add(renderObject);
      }
    } else {
      renderNode = new RenderNode(node, parent);
    }
    Optional<GLTFCamera> camera = node.getCamera();
    camera.ifPresent(this::useCamera);

    for (GLTFNode childNode : node.getChildren()) {
      processNodeChildren(childNode, renderNode);
    }
  }

  private void useCamera(GLTFCamera camera) {
    logger.info("Using file defined camera");
  }

  private void processGLTFMesh(GLTFMesh mesh, RenderNode parent) {
  }

  private void updateModelMatrix(Matrix4f modelMatrix, float scaleFactor) {
    this.modelMatrix.identity().scale(scaleFactor);
  }

  public static String getResourceAbsolutePath() {
    return new File("src/main/resources").getAbsolutePath();
  }

  public static void main(String[] args) {
    new SimpleViewer().run();
  }
}
