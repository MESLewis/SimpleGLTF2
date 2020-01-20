/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import java.io.InputStream;
import java.net.URI;

/**
 *
 */
public interface StreamIO {

  //TODO this should likely return a direct buffer instead to avoid
  //multiple copies
  InputStream getStreamForResource(URI uri);
}
