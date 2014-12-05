#import basic_imports;

uniform sampler2D u_texture;
uniform sampler2D u_light;
uniform float u_lightBlurIntensity;

#setting vec2 s_viewport;
#setting vec2 s_resolution;
#setting float s_time;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    vec4 lightColor = texture2D(u_light, gl_FragCoord.xy / s_resolution.xy);
    float intensity = (lightColor.r + lightColor.g + lightColor.b)/3.0 * u_lightBlurIntensity;

    vec2 offs = vec2(
        (
            sin(((gl_FragCoord.y + s_viewport.y) / s_resolution.y) * 8.0 + s_time * 0.7) * 0.01 +
            sin(((gl_FragCoord.x + s_viewport.x) / s_resolution.x) * 4.0 + s_time * 0.3) * 0.006
        ) * intensity,
        sin(((gl_FragCoord.x + s_viewport.x) / s_resolution.x) * 4.0 + s_time * 0.2) * 0.006 * intensity
    );
    vec2 texCoord = vec2(v_texCoord.x + offs.x, v_texCoord.y + offs.y);
    vec4 texColor = texture2D(u_texture, texCoord) * v_color;

    texColor.a = texColor.a * intensity;

    gl_FragColor = texColor;
}
