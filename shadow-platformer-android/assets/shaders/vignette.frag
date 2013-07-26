#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec2 resolution;

varying vec4 v_color;
varying vec2 v_texCoord;

const LOWP float RADIUS = 0.625;
const LOWP float SOFTNESS = 0.425;
const LOWP float OPACITY = 0.425;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    
    vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);
    float len = length(position);
    float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);
    texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, OPACITY);
    
    gl_FragColor = texColor;
}