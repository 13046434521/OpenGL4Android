attribute vec4 vPosition;
attribute vec2 a_texCoord;
varying vec2 tc;

void main() {
    tc = a_texCoord;
    gl_Position = vPosition;
}