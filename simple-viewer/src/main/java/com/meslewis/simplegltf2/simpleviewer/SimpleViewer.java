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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
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
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
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

  private GLTFImporter gltfImporter;
  private RenderNode rootRenderNode;
  private final RenderCamera renderCamera = new RenderCamera();

  private boolean wireframeMode = false; //Setting for showing wireframe. Toggled by 'w'
  private boolean limitedRender = false; //Setting - limits the number of primitives drawn
  private int limitedRenderIndex = 0; //Number of primitives to draw if in limited render mode

  private List<File> testFileList;
  private int nextTestFileIndex = 11;
  //Model 22 - CesiumMilkTruck - Good test for child node translations
  //Model 19 - buggy - More child node translation
  //Model 54 - Triangle without indices - Causing NPE
  //Model 11 - BoomBox with axes - child transforms not using matrix

  private boolean mouseDown = false;
  private float lastMouseX;
  private float lastMouseY;

  // The window handle
  private long window;

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
    window = glfwCreateWindow(RenderCamera.WIDTH, RenderCamera.HEIGHT, "Simple GLTF2 Viewer", NULL,
        NULL);
    if (window == NULL) { throw new RuntimeException("Failed to create the GLFW window"); }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
      if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE) {
        loadNextFile();
      }
      if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
        if (wireframeMode) {
          glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        } else {
          glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        wireframeMode = !wireframeMode;
      }
      if (key == GLFW_KEY_O && action == GLFW_RELEASE) {
        logger.debug("Toggle limited render");
        limitedRender = !limitedRender;
        limitedRenderIndex = 0;
      }
      if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
        //Normal p increases index
        logger.debug("Increasing limited render index");
        limitedRenderIndex++;
      }
    });

    glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
      renderCamera.zoom((float) yoffset);
    });

    glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
      if (button == GLFW_MOUSE_BUTTON_1) {
        if (action == GLFW_PRESS) {
          this.mouseDown = true;
        }
        if (action == GLFW_RELEASE) {
          this.mouseDown = false;
        }
      }
    });

    glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
      float deltaX = (float) (xpos - this.lastMouseX);
      float deltaY = (float) (ypos - this.lastMouseY);

      this.lastMouseX = (float) xpos;
      this.lastMouseY = (float) ypos;

      if (mouseDown) {
        renderCamera.rotate(deltaX, deltaY);
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

    //Set up file list from tests
    Path parentProject = Paths.get(new File(".").toURI()).getParent().getParent().toAbsolutePath();
    URI test = parentProject.toUri().resolve("core/src/test/resources/glTF-Sample-Models/");

    File modelsRoot = new File(test);

    ArrayList<File> fileList = new ArrayList<>();
    String defaultFilePath = "/default/chicken/Chicken.gltf";

    getAllFileChildren(modelsRoot, fileList);

    testFileList = fileList.stream()
        .filter(file -> file.getName().endsWith(".gltf"))
        .filter(file -> file.getParent().endsWith("glTF")) //Only load standard files for now
        .collect(Collectors.toList());

    testFileList.add(0, new File(SimpleViewer.getResourceAbsolutePath() + defaultFilePath));
  }

  private void loadNextFile() {
    File next = testFileList.get(nextTestFileIndex++);
    logger.info("==========================================================================");
    logger.info("Loading new model: " + (nextTestFileIndex - 1) + " " + next.getAbsolutePath());
    glfwSetWindowTitle(window, next.getName());
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

    glEnable(GL_DEPTH_TEST);

    //Need a default vertex array
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

    // Set the clear color
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

    loadNextFile();

    Renderer renderer = new Renderer();

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//      //TODO eyeZ was -1 but that made it flip around like crazy, 1 works for now but is probably backwards or something
//      viewMatrix.setLookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);
//      updateModelMatrix(modelMatrix, 0.001f * renderCamera.getAspectRatio());
//      viewMatrix.mul(modelMatrix, modelViewMatrix);
//      projectionMatrix.mul(viewMatrix, viewProjectionMatrix);

      if (limitedRender) {
        renderer.draw(renderCamera, rootRenderNode, limitedRenderIndex);
      } else {
        renderer.draw(renderCamera, rootRenderNode, -1);
      }

      glfwSwapBuffers(window); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }
  }


  private void loadFile(String path) {
    //Clear before loading
    rootRenderNode = new RenderNode(null, null);

    //Reset camera
//    RenderCamera.FOVY = 70f;
//    RenderCamera.Z_NEAR = 1f;
//    RenderCamera.Z_FAR = 1000f;
//    projectionMatrix.identity();
//    projectionMatrix.perspective(
//        RenderCamera.FOVY, aspectRatio, RenderCamera.Z_NEAR, RenderCamera.Z_FAR);

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
      logger.error("Extensions not supported. Loading next file");
      loadNextFile();
      return;
    }

    GLTFScene scene = gltf.getDefaultScene().orElseGet(() -> gltf.getScenes().get(0));

    for (GLTFNode rootNode : scene.getRootNodes()) {
      processNodeChildren(rootNode, this.rootRenderNode);
    }

    renderCamera.fitViewToScene(rootRenderNode);
  }

  private void processNodeChildren(GLTFNode node, RenderNode parent) {
    RenderNode renderNode;
    Optional<GLTFMesh> mesh = node.getMesh();
    if (mesh.isPresent()) {
      GLTFMesh gltfMesh = mesh.get();
      renderNode = new RenderNode(node, parent);
      for (GLTFMeshPrimitive primitive : gltfMesh.getPrimitives()) {
        logger.debug("Processing GLTFMesh. Name: " + gltfMesh.getName());
        //Each primitive gets its own render object
        new RenderObject(primitive, node, renderNode);
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

  //TODO migrate to RenderCamera
  private void useCamera(GLTFCamera camera) {
//    logger.info("Using file defined camera");
//    if (camera.getType() == GLTFCameraType.PERSPECTIVE) {
//      GLTFPerspective perspective = camera.getPerspective();
//
//      RenderCamera.FOVY = perspective.getYfov();
//      aspectRatio = perspective.getAspectRatio();
//      RenderCamera.Z_NEAR = perspective.getZnear();
//      RenderCamera.Z_FAR = perspective.getZfar();
//
//      projectionMatrix.identity();
//      projectionMatrix.perspective(
//          RenderCamera.FOVY, aspectRatio, RenderCamera.Z_NEAR, RenderCamera.Z_FAR);
//    } else {
//      logger.error("Unsupported camera type: " + camera.getType());
//    }
  }

  public static String getResourceAbsolutePath() {
    return new File("src/main/resources").getAbsolutePath();
  }

  public static void main(String[] args) {
    new SimpleViewer().run();
  }
}
