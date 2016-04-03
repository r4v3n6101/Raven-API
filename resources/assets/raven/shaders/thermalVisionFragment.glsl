#version 400 core

in vec2 pass_textureCoords;
out vec4 out_Color;

uniform sampler2D sampler;

void main(void) {
    vec4 pixcol = texture(sampler, pass_textureCoords);
    vec4 colors[3];
    colors[0] = vec4(0., 0., 1., 1.);
    colors[1] = vec4(1., 1., 0., 1.);
    colors[2] = vec4(1., 0., 0., 1.);
    float lum = (pixcol.r + pixcol.g + pixcol.b) / 3.;
    int ix = (lum < 0.5) ? 0:1;
    vec4 thermal = mix(colors[ix], colors[ix+1], (lum - float(ix) *0.5) / 0.5);
    out_Color = thermal;
}