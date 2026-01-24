#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    // Border width in UV coordinates (adjust this value to change border thickness)
    float borderWidth = 0.08;
    
    // Calculate distance from edges
    float distFromLeft = v_texCoords.x;
    float distFromRight = 1.0 - v_texCoords.x;
    float distFromBottom = v_texCoords.y;
    float distFromTop = 1.0 - v_texCoords.y;
    
    // Find minimum distance to any edge
    float minDistToEdge = min(min(distFromLeft, distFromRight), min(distFromBottom, distFromTop));
    
    // If we're near an edge and the pixel is opaque, make it white
    if (minDistToEdge < borderWidth && texColor.a > 0.1) {
        gl_FragColor = vec4(1.0, 1.0, 1.0, texColor.a) * v_color;
    } else {
        // Normal rendering
        gl_FragColor = texColor * v_color;
    }
}