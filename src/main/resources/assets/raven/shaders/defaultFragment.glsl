#version 400 core

in vec2 pass_textureCoords;
out vec4 out_Color;

uniform sampler2D sampler;

void main(void) {
    out_Color = texture(sampler, pass_textureCoords);
}