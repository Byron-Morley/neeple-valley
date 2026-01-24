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
    
    // Use the red channel as lookup value
    float lookupValue = baseColor.r;
    
    // Test with fixed coordinates - sample from top-left of ramp texture
    vec2 rampCoord = vec2(0.5, 0.0); // Middle x, top y
    vec4 rampColor = texture2D(u_colorRamp, rampCoord);
    
    // Apply ramp color but preserve original alpha
    rampColor.a = baseColor.a;
    
    gl_FragColor = rampColor * v_color;
}