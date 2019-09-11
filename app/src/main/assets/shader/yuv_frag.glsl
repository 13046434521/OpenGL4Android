precision mediump float;
uniform sampler2D tex_y;
uniform sampler2D tex_u;
varying vec2 tc;
void main() {
    float r, g, b, y, u, v;
    y = texture2D(tex_y, tc).r;
    vec4 uvData = texture2D(tex_u, tc);
    u = uvData.r - 0.5;
    v = uvData.a - 0.5;
    r = y + 1.13983*v;
    g = y - 0.39465*u - 0.58060*v;
    b = y + 2.03211*u;
    gl_FragColor = vec4(r, g, b, 1.0);
}