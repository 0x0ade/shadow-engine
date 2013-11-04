import basic_imports;

uniform sampler2D u_texture;

setting vec2 resolution;

LOWP varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;

    gl_FragColor = texColor;
}
