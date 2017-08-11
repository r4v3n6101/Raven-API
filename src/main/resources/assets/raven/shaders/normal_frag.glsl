#version 330

in vec2 pass_textureCoords;
in vec2 pass_lightmapCoords;
in mat3 tbn;
in vec3 pass_camPos;
in vec3 pass_position;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D lightmapSampler;
uniform sampler2D normalMapSampler;

const vec3 LIGHT_POS_1 = vec3(0.20000000298023224, 1, -0.699999988079071);
const vec3 LIGHT_POS_2 = vec3(-0.20000000298023224, 1, 0.699999988079071);

vec3 calculateColor(
    in vec3 color,
    in vec3 lightPos,
    in vec3 normal
){
    vec3 lightColor = texture(lightmapSampler, pass_lightmapCoords).rgb;

    vec3 tbn_lightPos = tbn * lightPos;
    vec3 tbn_pos = tbn * pass_position;
    vec3 lightDir = normalize(tbn_lightPos - tbn_pos);
    vec3 diffuse = lightColor * max(0, dot(lightDir, normal));

    return color * diffuse;
}

void main(void) {
    vec3 normal = texture(normalMapSampler, pass_textureCoords).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    vec3 color = texture(textureSampler, pass_textureCoords).rgb;

    vec3 finalColor;
    finalColor += calculateColor(color, LIGHT_POS_1, normal);
    finalColor += calculateColor(color, LIGHT_POS_2, normal);
    out_Color = vec4(finalColor, 1f);
}