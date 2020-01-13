#version 330 core

uniform mat4 u_ModelViewProjection;

in vec4 a_Position;
out vec4 vertexColor;

void main() {
  gl_Position = u_ModelViewProjection * a_Position;
  vertexColor = vec4(1.0, 1.0, 0.0, 1.0);
}