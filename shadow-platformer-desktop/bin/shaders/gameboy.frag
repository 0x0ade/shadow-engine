#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;

uniform vec2 resolution;

varying LOWP vec4 v_color;
varying vec2 v_texCoord;

const LOWP float RADIUS = 0.625;
const LOWP float SOFTNESS = 0.425;
const LOWP float OPACITY = 0.425;

const vec3 SEPIA = vec3(0.8, 1.2, 0.8); 

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord) * v_color;
    
    vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);
    float len = length(position);
    float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);
    texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, OPACITY);
    
    //convert to grayscale using NTSC conversion weights
    float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    
    vec3 sepiaColor = vec3(gray) * SEPIA;
    
    texColor.rgb = mix(texColor.rgb, sepiaColor, 1.0);
    
    gl_FragColor = texColor;
}