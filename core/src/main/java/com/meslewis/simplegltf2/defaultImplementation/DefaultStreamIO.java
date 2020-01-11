/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.defaultImplementation;

import com.meslewis.simplegltf2.StreamIO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.net.URI;


public class DefaultStreamIO implements StreamIO {

  @Override
  public InputStream getStreamForResource(URI uri) {
    try {
      return new FileInputStream(uri.getPath());
    } catch (FileNotFoundException e) {
      throw new IOError(new Throwable("Could not get input stream for file: " + uri.toString()));
    }
  }
}
