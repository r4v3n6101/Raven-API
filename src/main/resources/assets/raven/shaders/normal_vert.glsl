#version 330

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

out vec2 pass_textureCoords;
out vec2 pass_lightmapCoords;
out vec3 pass_camPos;
out vec3 pass_position;
out mat3 tbn;

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
    tbn = transpose(mat3(
        normalize(normal),
        normalize(tangent),
        normalize(bitangent)
    ));
}