#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;


void main(void) {
    vec4 worldPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = viewMatrix * projectionMatrix * worldPosition;
    pass_textureCoords = textureCoords;
}