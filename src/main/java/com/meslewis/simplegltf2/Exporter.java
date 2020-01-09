package com.meslewis.simplegltf2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Exporter {

  public static void export(String path, String fileName, Object obj) {
    ObjectMapper mapper = new ObjectMapper();

    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    File output = new File(path, fileName);

    try {
      output.createNewFile();
      mapper.writeValue(output, obj);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
