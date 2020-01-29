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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class RenderToImageTest {

  private static final int WINDOW_WIDTH = 1280;
  private static final int WINDOW_HEIGHT = 720;


  @TestFactory
  public Collection<DynamicTest> generateUnitTestImages() {
    List<File> files = new ArrayList<>();

    String testPath = "src/test/resources/assetGenerator/Positive";

    getAllFileChildren(new File(testPath), files);
    files = files.stream()
        .filter(file -> file.getName().endsWith(".gltf") || file.getName().endsWith(".glb"))
        .filter(File::isFile)
        .filter(file -> file.length() < 100000) //Don't load files that are too big
        .collect(Collectors.toList());

    File destination = new File("build/images/assetGenerator/Positive");
    return generateImagesFromModels(files, destination);
  }

  private Collection<DynamicTest> generateImagesFromModels(List<File> files, File destination) {
    SimpleViewer viewer = new SimpleViewer();
    viewer.setupNativeWindow();
    viewer.init();
    destination.mkdirs();

    return files.stream()
        .map(file -> DynamicTest.dynamicTest(
            "Generating image for file: " + file.getParentFile().getName() + file.getName(),
            () -> {
              viewer.loadFile(file);
              viewer.renderFrame();
              saveScreenToImage(file.getName().replace(".", "") + file.getParentFile().getName(),
                  destination); //TODO dynamic file name
            }))
        .collect(Collectors.toList());
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
