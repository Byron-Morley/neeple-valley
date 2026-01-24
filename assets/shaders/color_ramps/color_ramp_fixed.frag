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

    // Use the red channel as lookup value, but invert it
    float lookupValue = 1.0 - baseColor.r;

    // Map the grayscale value to one of 6 discrete positions in the ramp
    // The ramp is 12 pixels wide (6 colors × 2 pixels each)
    // Sample from the center of each 2-pixel color: positions 1, 3, 5, 7, 9, 11
    int colorIndex = int(lookupValue * 5.0 + 0.5); // 0-5 range
    float rampX = (1.0 + float(colorIndex) * 2.0) / 12.0; // Convert to normalized coordinates
    
    // Sample from the center Y of the ramp
    vec2 rampCoord = vec2(rampX, 0.5);
    vec4 rampColor = texture2D(u_colorRamp, rampCoord);

    // Preserve alpha from original sprite
    rampColor.a *= baseColor.a;

    gl_FragColor = rampColor * v_color;
}