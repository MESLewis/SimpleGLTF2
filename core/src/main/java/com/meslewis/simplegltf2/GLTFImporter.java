/*
 * Copyright (c) 2020. Morgan Lewis <https://github.com/MESLewis>
 * This file is part of the SimpleGLTF2 project which is released under the MIT License.
 * See <https://github.com/MESLewis/SimpleGLTF2/blob/master/LICENSE> for more information
 */

package com.meslewis.simplegltf2;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.meslewis.simplegltf2.data.GLTF;
import com.meslewis.simplegltf2.defaultImplementation.DefaultStreamIO;
import java.io.IOException;
import java.net.URI;

public class GLTFImporter {

  /**
   * Default GLTFImporter instance
   */
  public static final GLTFImporter instance = new GLTFImporter();

  private StreamIO streamIO = new DefaultStreamIO();

  private ObjectMapper mapper = new ObjectMapper();

  public GLTF load(URI uri) throws IOException {
    GLTF gltf = new GLTF(streamIO, uri);

    ObjectReader reader = mapper.setInjectableValues(injectGLTF(gltf)).readerForUpdating(gltf);
    reader.readValue(streamIO.getStreamForResource(uri));

    return gltf;
  }

  /**
   * Inject reference to base GLTF into each node, used for index resolution Also a
   * RelativePathResolver, called to get a stream from a path
   *
   * @param gltf
   * @return
   */
  private InjectableValues injectGLTF(GLTF gltf) {
    InjectableValues.Std iv = new InjectableValues.Std();
    iv.addValue(GLTF.class, gltf);
    return iv;
  }

  public void setStreamIO(StreamIO streamIO) {
    this.streamIO = streamIO;
  }

  StreamIO getStreamIO() {
    return this.streamIO;
  }
}