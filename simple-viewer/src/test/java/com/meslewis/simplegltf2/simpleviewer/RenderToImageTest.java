/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.simpleviewer;

import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class RenderToImageTest {

  private static final int WINDOW_WIDTH = 400;
  private static final int WINDOW_HEIGHT = 400;

  private static SimpleViewer viewer;
  private String absoluteLoadBase;

  private static class AssetManifest {

    @JsonProperty("folder")
    String folder;

    @JsonProperty("id")
    int id;

    @JsonProperty("models")
    AssetModel[] models;

    public AssetManifest() {
    }
  }

  private static class AssetModel {

    @JsonProperty("fileName")
    String fileName;

    @JsonProperty("sampleImageName")
    String sampleImageName;

    @JsonProperty("loadable")
    boolean loadable;

    float[] cameraPos;

    @JsonSetter("camera")
    void setCameraPos(JsonNode jsonNode) {
      ArrayNode arrayNode = (ArrayNode) jsonNode.get("translation");
      cameraPos = new float[arrayNode.size()];
      for (int i = 0; i < arrayNode.size(); i++) {
        cameraPos[i] = arrayNode.get(i).floatValue();
      }
    }

    public AssetModel() {
    }
  }

  @BeforeAll
  static void setup() {
    SimpleViewer.WIDTH = WINDOW_WIDTH;
    SimpleViewer.HEIGHT = WINDOW_HEIGHT;
    viewer = new SimpleViewer();
    viewer.setupNativeWindow();
    viewer.init();
  }

  @TestFactory
  Collection<DynamicTest> generateUnitTestImages() {
    absoluteLoadBase = "";
    try {
      absoluteLoadBase =
          new File("../sample-models/glTF-Asset-Generator/Output/Positive/").getCanonicalFile()
              .getAbsolutePath() + '/';
    } catch (IOException e) {
      e.printStackTrace();
    }
    File manifestFile = new File(absoluteLoadBase + "Manifest.json");
    ObjectMapper mapper = new ObjectMapper();
    AssetManifest[] manifests = new AssetManifest[0];
    try {
      manifests = mapper.readValue(manifestFile, AssetManifest[].class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    File dest = new File("build/test/images/");
    Collection<DynamicTest> tests = new ArrayList<>();
    for (AssetManifest m : manifests) {
      tests.addAll(generateImagesFromModels(m, dest));
    }
    return tests;
  }

  private Collection<DynamicTest> generateImagesFromModels(AssetManifest manifest,
      File destination) {
    destination.mkdirs();

    AssetModel[] models = manifest.models;

    return Arrays.stream(models)
        .map(model -> DynamicTest.dynamicTest(
            String.format("Generating image for file: %s", model.fileName),
            () -> {
              viewer.loadFile(new File(absoluteLoadBase + manifest.folder + '/' + model.fileName));
              viewer.getRenderCamera().getPosition()
                  .set(model.cameraPos[0], model.cameraPos[1], model.cameraPos[2]);
              viewer.renderFrame();
              String outputName = model.fileName.substring(0, model.fileName.indexOf("."));
              saveScreenToImage(outputName, destination);
              moveSampleImage(manifest, model, destination);
            }))
        .collect(Collectors.toList());
  }

  private void moveSampleImage(AssetManifest manifest, AssetModel model, File outputFolder) {
    if (model.sampleImageName == null || model.sampleImageName.isEmpty()) {
      return; //Not all models have sample images
    }
    String sampleImagePath = String
        .format("%s/%s/%s", absoluteLoadBase, manifest.folder, model.sampleImageName);
    String sampleFileName = model.sampleImageName
        .substring(model.sampleImageName.lastIndexOf('/') + 1);
    File sample = new File(sampleImagePath);
    String destName = String.format("%s/%s_sample%s", outputFolder,
        sampleFileName.substring(0, sampleFileName.indexOf('.')),
        sampleFileName.substring(sampleFileName.indexOf('.')));
    File dest = new File(destName);
    try {
      Files.copy(sample.toPath(), dest.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //TODO animation / gif
  private void saveScreenToImage(String name, File outputFolder) {
    glReadBuffer(GL_FRONT);

    ByteBuffer buffer = ByteBuffer.allocateDirect(WINDOW_HEIGHT * WINDOW_WIDTH * 4);
    glReadPixels(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

    String format = "png";
    File file = new File(outputFolder.toURI().resolve(name + "." + format));
    BufferedImage image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT,
        BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < WINDOW_WIDTH; x++) {
      for (int y = 0; y < WINDOW_HEIGHT; y++) {
        int i = (x + (WINDOW_WIDTH * y)) * 4;
        int r = buffer.get(i) & 0xFF;
        int g = buffer.get(i + 1) & 0xFF;
        int b = buffer.get(i + 2) & 0xFF;
        image.setRGB(x, WINDOW_HEIGHT - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
      }
    }

    try {
      ImageIO.write(image, format, file);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
