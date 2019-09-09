attribute vec4 a_Position;
uniform mat4 a_MvpMatrix;
varying vec3 v_TexCoord;

void main() {
    gl_Position=a_MvpMatrix * a_Position;
    v_TexCoord=vec3(a_Position.xyz);
}
