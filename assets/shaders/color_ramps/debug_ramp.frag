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
    
    // Debug: Show the red channel as grayscale
    // This will help us see what lookup values we're getting
    float lookupValue = baseColor.r;
    vec4 debugColor = vec4(lookupValue, lookupValue, lookupValue, baseColor.a);
    
    gl_FragColor = debugColor * v_color;
}