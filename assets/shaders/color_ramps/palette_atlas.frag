#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_colorRamp;
uniform float u_rampIndex;
uniform vec4 u_rampRegion; // x, y, width, height of the ramp in atlas coordinates
uniform float u_time;

varying vec4 v_color;
varying vec2 v_texCoords;

void main() {
    vec4 baseColor = texture2D(u_texture, v_texCoords);

    // Use the red channel as lookup value (brightness), but invert it
    float lookupValue = 1.0 - baseColor.r;
    
    // Map the grayscale value (0.0-1.0) to one of the 6 discrete color indices (0-5)
    int colorIndex = int(lookupValue * 5.0 + 0.5); // Round to nearest index
    
    // Each ramp has 6 discrete color positions in the atlas
    // u_rampRegion.x is the starting X position of index 0
    // Colors are spaced 4 pixels apart: indices at X positions 0, 4, 8, 12, 16, 20
    float rampX = u_rampRegion.x + float(colorIndex * 4) / 1024.0;
    float rampY = u_rampRegion.y + u_rampRegion.w * 0.5; // Center Y of the ramp region
    
    // Sample from the atlas at the exact discrete position
    vec2 rampCoord = vec2(rampX, rampY);
    vec4 rampColor = texture2D(u_colorRamp, rampCoord);

    // Preserve alpha from original sprite
    rampColor.a *= baseColor.a;

    gl_FragColor = rampColor * v_color;
}