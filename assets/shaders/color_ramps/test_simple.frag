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
    
    // Simple test - just tint the hair red
    vec4 testColor = vec4(1.0, 0.0, 0.0, baseColor.a);
    
    gl_FragColor = testColor * v_color;
}