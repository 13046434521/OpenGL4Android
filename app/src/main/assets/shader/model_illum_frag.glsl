
precision mediump float;
varying vec2 v_TexCoord;
uniform sampler2D u_TextureUnit;
varying vec4 vDiffuse;          //接收从顶点着色器过来的散射光分量
varying vec4 vAmbient;          //接收传递给片元着色器的环境光分量
varying vec4 vSpecular;         //接收传递给片元着色器的镜面光分量
void main() {
    vec4 finalColor=texture2D(u_TextureUnit,v_TexCoord);
    //       //给此片元颜色值
    //    gl_FragColor=finalColor;
    //vec4 finalColor=vec4(1.0);
    //给此片元颜色值
    gl_FragColor=finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;
}