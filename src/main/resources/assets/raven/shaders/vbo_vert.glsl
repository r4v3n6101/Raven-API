#version 330

in vec3 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;
out vec2 pass_lightmapCoords;
out vec3 pass_camPos;
out vec3 pass_position;

uniform mat4 modelViewProjectionMatrix;
uniform vec2 lightmapCoords;
uniform vec3 camPos;

void main(void) {
    vec4 pos = modelViewProjectionMatrix * vec4(position, 1.0);
    gl_Position = pos;
    pass_textureCoords = textureCoords;
    pass_lightmapCoords = lightmapCoords;
    pass_camPos = camPos;
    pass_position = pos.xyz;
}