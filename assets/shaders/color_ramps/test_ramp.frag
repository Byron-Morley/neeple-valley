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
    
    // Use the red channel (or luminance) as lookup value
    float lookupValue = baseColor.r;
    
    // Sample from color ramp texture - try different coordinates
    vec2 rampCoord = vec2(lookupValue, u_rampIndex);
    vec4 rampColor = texture2D(u_colorRamp, rampCoord);
    
    // Debug: Show both original and ramp color
    // If ramp sampling fails, show original color
    if (rampColor.a < 0.1) {
        // Ramp color is transparent, use original
        gl_FragColor = baseColor * v_color;
    } else {
        // Use ramp color but preserve original alpha
        rampColor.a = baseColor.a;
        gl_FragColor = rampColor * v_color;
    }
}