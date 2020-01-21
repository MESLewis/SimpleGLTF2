/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Returns a direct ByteBuffer from a URI
 */
public interface BufferIO {

  ByteBuffer getDirectByteBuffer(URI uri);
}
