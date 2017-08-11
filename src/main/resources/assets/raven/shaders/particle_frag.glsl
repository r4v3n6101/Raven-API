#version 330

in vec2 pass_textureCoords;
in vec2 pass_lightmapCoords;
in vec4 pass_Color;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D lightmapSampler;

void main(void) {
    out_Color = pass_Color * texture(lightmapSampler, pass_lightmapCoords) * texture(textureSampler, pass_textureCoords);
}