module com.meslewis.simplegltf.core {
  exports com.meslewis.simplegltf2.data;
  exports com.meslewis.simplegltf2.defaultImplementation;
  exports com.meslewis.simplegltf2;
  requires com.fasterxml.jackson.annotation;
  requires validation.api;
  requires com.fasterxml.jackson.databind;
  requires slf4j.api;
}