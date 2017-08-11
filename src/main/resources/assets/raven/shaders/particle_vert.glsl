#version 330
/**Static**/
in vec3 position;

/**Dynamic**/
in vec4 textureCoords;
in vec3 translate;
in vec3 rotation;
in vec3 scale;
in vec2 billboardRotation;
in vec2 lightmapCoords;
in vec4 color;

out vec2 pass_textureCoords;
out vec2 pass_lightmapCoords;
out vec4 pass_Color;

uniform mat4 modelViewProjectionMatrix;

mat4 scaleMat(mat4 inMat, vec3 inVec){
   mat4 scMat = mat4(
                     inVec.x, 0, 0, 0,
                     0, inVec.y, 0, 0,
                     0, 0, inVec.z, 0,
                     0, 0, 0, 1
                     );
    return inMat * transpose(scMat);
}
mat4 translateMat(mat4 inMat, vec3 inVec){
    mat4 trMat = mat4(
                    1, 0, 0, inVec.x,
                    0, 1, 0, inVec.y,
                    0, 0, 1, inVec.z,
                    0, 0, 0, 1
                    );
    return inMat * transpose(trMat);
}
mat4 rotateMat(mat4 inMat, vec3 inVec){
    mat4 outMat = inMat;
    if(inVec.x != 0){
        float angle = radians(inVec.x);
        float cos = cos(angle);
        float sin = sin(angle);
        mat4 xMat = mat4(
                        1, 0, 0, 0,
                        0, cos, -sin, 0,
                        0, sin, cos, 0,
                        0, 0, 0, 1
                        );
        outMat = outMat * transpose(xMat);
    }
    if(inVec.y != 0){
        float angle = radians(inVec.y);
        float cos = cos(angle);
        float sin = sin(angle);
        mat4 yMat = mat4(
                        cos, 0, sin, 0,
                        0, 1, 0, 0,
                        -sin, 0, cos, 0,
                        0, 0, 0, 1
                        );
        outMat = outMat * transpose(yMat);
    }
    if(inVec.z != 0){
        float angle = radians(inVec.z);
        float cos = cos(angle);
        float sin = sin(angle);
        mat4 zMat = mat4(
                        cos, -sin, 0, 0,
                        sin, cos, 0, 0,
                        0, 0, 1, 0,
                        0, 0, 0, 1
                        );
        outMat = outMat * transpose(zMat);
    }
    return outMat;
}

void main(void) {
    mat4 mvpMat = modelViewProjectionMatrix;
    mvpMat = translateMat(mvpMat, translate);

    mvpMat = rotateMat(mvpMat, vec3(0, billboardRotation.y,0));
    mvpMat = rotateMat(mvpMat, vec3(billboardRotation.x, 0, 0));
    mvpMat = rotateMat(mvpMat, rotation);

    mvpMat = scaleMat(mvpMat, scale);

    gl_Position = mvpMat * vec4(position, 1.0);
    float u;
    float v;
    //Set up u v
    if (position.x < 0){
    	u = textureCoords.x;
    } else {
    	u = textureCoords.y;
    }
    if (position.y > 0){
    	v = textureCoords.z;
    } else {
    	v = textureCoords.w;
    }
    pass_textureCoords = vec2(u, v);
    pass_lightmapCoords = lightmapCoords;
    pass_Color = color;
}