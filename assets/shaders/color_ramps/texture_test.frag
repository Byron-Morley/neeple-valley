#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_colorRamp;
uniform float u_rampIndex;
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 baseColor = texture2D(u_texture, v_texCoords);
    
    // Test if we can access the color ramp texture at all
    // Sample the ramp texture and use it as a tint
    vec4 rampSample = texture2D(u_colorRamp, vec2(0.5, 0.1));
    
    // If ramp sample is valid, tint the sprite with it
    // If not, the sprite will appear normal
    vec4 result = baseColor;
    if (rampSample.a > 0.1) {
        // Mix the base color with the ramp sample
        result = vec4(baseColor.rgb * rampSample.rgb, baseColor.a);
    }
    
    gl_FragColor = result * v_color;
}