package com.meslewis.simplegltf2;

import java.io.InputStream;
import java.net.URI;

/**
 *
 */
public interface StreamIO {

  InputStream getStreamForResource(URI uri);
}
