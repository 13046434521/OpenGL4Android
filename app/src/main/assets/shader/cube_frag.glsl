precision mediump float;
uniform samplerCube u_TextureUnit;
varying vec3 v_TexCoord;
void main() {
        gl_FragColor=textureCube(u_TextureUnit, v_TexCoord);

        //    gl_FragColor=vec4(1, 0, 0, 1);
}
