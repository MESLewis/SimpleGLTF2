/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * A buffer points to binary geometry, animation, or skins. TODO some buffers can be copied directly
 * to GPU
 */
public class GLTFBuffer extends GLTFChildOfRootProperty {

  /**
   * The uri of the buffer. Relative paths are relative to the .gltf file. Instead of referencing an
   * external file, the uri can also be a data-uri.
   */
  @JsonProperty("uri")
  private URI uri;

  /**
   * The length of the buffer in bytes.
   */
  @JsonProperty("byteLength")
  @NotNull
  @Min(1)
  private Integer byteLength;

  public URI getUri() {
    return uri;
  }

  public Integer getByteLength() {
    return byteLength;
  }

  /**
   * Java nio Buffer holding data TODO probably use MappedByteBuffer in the future for speed
   */
  private java.nio.ByteBuffer buffer;

  /**
   * @return the String for this buffer's URI
   */
  public String getScheme() {
    return this.uri.getScheme();
  }

  /**
   * Load the data referenced by this Buffer into a java.nio.Buffer
   *
   * @return java.nio.Buffer with relevant data
   * TODO endianness
   */
  public ByteBuffer getData(int start, int length) throws IOException {
    if (start + length > this.byteLength) {
      throw new BufferUnderflowException();
    }
    if (buffer == null) {
      resolveBufferData();
    }

//    byte[] destination = new byte[length];
//    buffer.position(start);
//    buffer.get(destination, 0, length);
//    return ByteBuffer.wrap(destination);
    return buffer.slice(start, length); //This causes errors if using lwjglx debugger
  }

  /**
   * Loads the buffer according to uri
   *
   * @throws IOException
   */
  private void resolveBufferData() throws IOException {
    InputStream stream = URIUtil.getStreamFromGeneralURI(gltf, uri);
    byte[] allBytes = stream.readAllBytes();
    buffer = ByteBuffer.allocateDirect(allBytes.length);
    buffer.put(allBytes).order(ByteOrder.LITTLE_ENDIAN);
  }
}
