/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.defaultImplementation;

import com.meslewis.simplegltf2.BufferIO;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;


public class DefaultBufferIO implements BufferIO {

  @Override
  public ByteBuffer getDirectByteBuffer(URI uri) {
    try (FileChannel fileChanel = (FileChannel) Files
        .newByteChannel(Paths.get(uri), EnumSet.of(StandardOpenOption.READ))) {
      return fileChanel.map(MapMode.READ_ONLY, 0, fileChanel.size());
    } catch (IOException e) {
      throw new IOError(new Throwable("Could not get input stream for file: " + uri.toString()));
    }
  }
}
