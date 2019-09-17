
attribute vec3 a_Position;
attribute vec2 a_TexCoord;
varying vec2 v_TexCoord;
uniform mat4 u_MvpMatrix;
void main(){
    gl_Position = u_MvpMatrix*vec4(a_Position, 1);
    v_TexCoord = a_TexCoord;
}