uniform mat4 u_MvpMatrix;
attribute vec3 a_Position;
varying vec3 v_Position;
void main() {
    v_Position = a_Position;
    v_Position.z = -v_Position.z;//反转Z，右手坐标系，变为左手坐标系
    gl_Position = u_MvpMatrix * vec4(a_Position, 1);//W设置为1
    gl_Position = gl_Position.xyww;//把Z值变成1，这样透视除法之后为1，即Z始终在1的远平面上。Z=1最远，渲染出来的纹理，永远在最后面
}
