#version 330

in vec2 pass_textureCoords;
in vec2 pass_lightmapCoords;
out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D lightmapSampler;

void main(void) {
    out_Color = texture(textureSampler, pass_textureCoords) * texture(lightmapSampler, pass_lightmapCoords);
}