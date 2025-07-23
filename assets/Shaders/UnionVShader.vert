//uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
varying vec2 texCoord;

void main() {
    texCoord = inTexCoord;
    gl_Position = vec4(inPosition, 1.0);
}