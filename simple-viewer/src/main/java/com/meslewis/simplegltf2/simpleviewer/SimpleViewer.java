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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
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
import static org.lwjgl.glfw.GLFW.glfwSetDropCallback;
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
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER_DERIVATIVE_HINT;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.meslewis.simplegltf2.GLTFImporter;
import com.meslewis.simplegltf2.data.GLTF;
import com.meslewis.simplegltf2.data.GLTFAnimation;
import com.meslewis.simplegltf2.data.GLTFCamera;
import com.meslewis.simplegltf2.data.GLTFMesh;
import com.meslewis.simplegltf2.data.GLTFMeshPrimitive;
import com.meslewis.simplegltf2.data.GLTFNode;
import com.meslewis.simplegltf2.data.GLTFScene;
import com.meslewis.simplegltf2.simpleviewer.render.RenderCamera;
import com.meslewis.simplegltf2.simpleviewer.render.RenderMesh;
import com.meslewis.simplegltf2.simpleviewer.render.RenderMeshPrimitive;
import com.meslewis.simplegltf2.simpleviewer.render.RenderNode;
import com.meslewis.simplegltf2.simpleviewer.render.Renderer;
import com.meslewis.simplegltf2.simpleviewer.render.animation.RenderAnimation;
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
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Initial configuration from https://www.lwjgl.org/guide
public class SimpleViewer {

  public static final int WIDTH = 1280;
  public static final int HEIGHT = 720;
  private static final Logger logger = LoggerFactory.getLogger(SimpleViewer.class);
  private static final String resourceAbsolutePath = new File("src/main/resources")
      .getAbsolutePath();
  private static final URI resourceAbsoluteURI = new File(resourceAbsolutePath).toURI();

  private GLTFImporter gltfImporter;
  private RenderNode rootRenderNode;
  private List<RenderAnimation> animations = new ArrayList<>();
  private final RenderCamera renderCamera = new RenderCamera();

  private long animationStartTime;

  private boolean wireframeMode = false; //Setting for showing wireframe. Toggled by 'w'
  private boolean limitedRender = false; //Setting - limits the number of primitives drawn
  private int limitedRenderIndex = 0; //Number of primitives to draw if in limited render mode

  private SampleFileType sampleType = SampleFileType.GLTF_STANDARD;
  private List<File> testFileList;
  private int nextTestFileIndex = 0;
  //Standard - 58 - Water bottle
  //Standard - 1  - Alpha blend test
  //Standard - 24 - Damaged Helmet
  //Standard - 10 - Boombox with axis.
  //Standard - 20 - Cesium man
  //Standard - 16 - Box vertex colors
  //Standard - 57 - Vertex color test
  //Standard - 13 - Box Interleaved
  //Standard - 15 - Textured non power of two TODO resize textures if not power of two
  //Standard - 27 - Flight helmet
  //Standard - 29 - Interpolation test
  //Standard - 51 - Texture Transform Test - Texture transform extension
  //Standard - 5  - Animated triangle TODO animation
  //Standard - 3  - Morph cube
  //Standard - 44 - Simple morph
  //TODO animation

  private boolean mouseDown = false;
  private float lastMouseX;
  private float lastMouseY;

  // The window handle
  private long window;
  private Renderer renderer;

  public void run() {
    setupNativeWindow();
    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  void setupNativeWindow() {
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
    window = glfwCreateWindow(WIDTH, HEIGHT, "Simple GLTF2 Viewer", NULL,
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
      if (key == GLFW_KEY_T && action == GLFW_RELEASE) {
        int next = (sampleType.ordinal() + 1) % SampleFileType.values().length;
        this.sampleType = SampleFileType.values()[next];
        logger.debug("Changing sample model list type to " + sampleType.toString());
        loadSampleFiles();
        nextTestFileIndex = 0;
        loadNextFile();
      }
      if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
        ShaderDebugType dType = renderer.getDebugType();
        int next = (dType.ordinal() + 1) % ShaderDebugType.values().length;
        renderer.setDebugType(ShaderDebugType.values()[next]);
        logger.debug("Render debug type: " + renderer.getDebugType().name());
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

    glfwSetDropCallback(window, (window, count, names) -> {
      String[] nameStrings = new String[count];
      for (int i = 0; i < count; i++) {
        nameStrings[i] = GLFWDropCallback.getName(names, i);
      }
      logger.info("Dropped files");
      Optional<String> baseFile = Arrays.stream(nameStrings)
          .filter(s -> s.endsWith(".gltf") || s.endsWith(".glb"))
          .findFirst();
      baseFile.ifPresent(s -> loadFile(new File(s)));
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
  }

  void init() {
    gltfImporter = new GLTFImporter();

    loadSampleFiles();

    GL.createCapabilities();
    GLUtil.setupDebugMessageCallback();

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);
    glColorMask(true, true, true, true);
    glClearDepth(1.0);

    glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT,
        GL_NICEST); //Use a nicer calculation in fragment shaders

    //Need a default vertex array
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);

    // Set the clear color
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    loadNextFile();

    renderer = new Renderer();
  }

  private void loadSampleFiles() {
    //Set up file list from tests
    Path parentProject = Paths.get(new File(".").toURI()).getParent().getParent().toAbsolutePath();
    URI test = parentProject.toUri().resolve("core/src/test/resources/glTF-Sample-Models/");

    File modelsRoot = new File(test);

    ArrayList<File> fileList = new ArrayList<>();
    String defaultFilePath = "/default/chicken/Chicken.gltf";

    getAllFileChildren(modelsRoot, fileList);

    testFileList = fileList.stream()
        .filter(sampleType::filter)
        .collect(Collectors.toList());

    testFileList.add(testFileList.size(),
        new File(SimpleViewer.getResourceAbsolutePath() + defaultFilePath));
  }

  private void loadNextFile() {
    File next = testFileList.get(nextTestFileIndex++);
    if (nextTestFileIndex >= testFileList.size()) {
      nextTestFileIndex = 0;
    }
    logger.info("==========================================================================");
    logger.info("Loading new model: " + (nextTestFileIndex - 1) + " " + next.getAbsolutePath());
    glfwSetWindowTitle(window, next.getPath());
    renderCamera.reset();
    loadFile(next);
    logger.info(
        "Finished Loading new model: " + (nextTestFileIndex - 1) + " " + next.getAbsolutePath());
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
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      renderFrame();
    }
  }

  /**
   * Render a single frame to the window
   */
  void renderFrame() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

    prepareSceneForRendering();

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

  private void prepareSceneForRendering() {
    animateNode();
    rootRenderNode.applyTransform(new Matrix4f());
  }

  //  private static float debugStep = -0.25f;
  private void animateNode() {
    float animationTimeScale = 0.5f;
    float elapsedTime =
        (System.currentTimeMillis() - animationStartTime) / 1000f * animationTimeScale;
//    debugStep += 0.25f;
//    float elapsedTime = debugStep;

    //TODO selecting animation

    for (RenderAnimation anim : animations) {
      anim.advance(elapsedTime);
    }
  }

  public RenderNode getRenderNode(GLTFNode target) {
    return getRenderNodeHelper(target, rootRenderNode);
  }

  private RenderNode getRenderNodeHelper(GLTFNode target, RenderNode test) {
    if (target.equals(test.getGltfNode())) {
      return test;
    }
    RenderNode ret = null;
    for (RenderNode nextTest : test.getChildren()) {
      ret = getRenderNodeHelper(target, nextTest);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }

  void loadFile(File file) {
    //Clear before loading
    rootRenderNode = new RenderNode(null, null);
    animations.clear();

    GLTF gltf;
    try {
      URI uri = file.toURI();
      gltf = gltfImporter.load(uri);
      if (gltf == null) {
        return;
      }
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

    //Generate RenderNodes for scene
    for (GLTFNode rootNode : scene.getRootNodes()) {
      processNodeChildren(rootNode, this.rootRenderNode);
    }

    //Generate Animations
    if (gltf.getAnimations() != null) {
      for (GLTFAnimation animation : gltf.getAnimations()) {
        animations.add(new RenderAnimation(animation, this));
      }
    }

    Matrix4f sceneScale = new Matrix4f();
    rootRenderNode.applyTransform(sceneScale);

//    AABBf sceneExtends = new AABBf();
//    renderCamera.getSceneExtends(rootRenderNode, sceneExtends);
//    float minValue = Math.min(sceneExtends.minX, Math.min(sceneExtends.minY, sceneExtends.minZ));
//    float maxValue = Math.max(sceneExtends.maxX, Math.max(sceneExtends.maxY, sceneExtends.maxZ));
//    float delta = 1 / (maxValue - minValue);
//    sceneScale.scale(delta);
//    rootRenderNode.applyTransform(sceneScale);
//    logger.info("Scaling scene by " + delta);

    renderCamera.fitViewToScene(rootRenderNode);

    animationStartTime = System.currentTimeMillis();
  }

  private void processNodeChildren(GLTFNode node, RenderNode parent) {
    RenderNode renderNode;
    Optional<GLTFMesh> mesh = node.getMesh();
    if (mesh.isPresent()) {
      GLTFMesh gltfMesh = mesh.get();
      renderNode = new RenderMesh(node, parent);
      for (GLTFMeshPrimitive primitive : gltfMesh.getPrimitives()) {
        logger.debug("Processing GLTFMesh. Name: " + gltfMesh.getName());
        //Each primitive gets its own render object.
        new RenderMeshPrimitive(primitive, null, (RenderMesh) renderNode);
      }
    } else {
      renderNode = new RenderNode(node, parent);
    }
    Optional<GLTFCamera> camera = node.getCamera();
    camera.ifPresent(renderCamera::setGLTFCamera);

    for (GLTFNode childNode : node.getChildren()) {
      processNodeChildren(childNode, renderNode);
    }
  }

  public static String getResourceAbsolutePath() {
    return resourceAbsolutePath;
  }

  public static URI getResourceAbsoluteURI() {
    return resourceAbsoluteURI;
  }

  public static void main(String[] args) {
    new SimpleViewer().run();
  }
}
