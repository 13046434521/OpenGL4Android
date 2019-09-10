precision mediump float;
uniform sampler2D y_TextureUnit;
uniform sampler2D u_TextureUnit;
uniform sampler2D v_TextureUnit;

varying vec2 v_TexCoord;
void main() {
    vec4 c = vec4((texture2D(y_TextureUnit, v_TexCoord).r - 16./255.) * 1.164);
    vec4 U = vec4(texture2D(u_TextureUnit, v_TexCoord).r - 128./255.);
    vec4 V = vec4(texture2D(v_TextureUnit, v_TexCoord).r - 128./255.);
    c += V * vec4(1.596, -0.813, 0, 0);
    c += U * vec4(0, -0.392, 2.017, 0);
    c.a = 1.0;
    gl_FragColor = c;
}