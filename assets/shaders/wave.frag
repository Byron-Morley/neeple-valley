#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_time;

void main() {
    // Create a wave effect by offsetting the texture coordinates
    vec2 waveCoords = v_texCoords;
    waveCoords.x += sin(v_texCoords.y * 10.0 + u_time * 5.0) * 0.02;
    waveCoords.y += cos(v_texCoords.x * 10.0 + u_time * 3.0) * 0.01;
    
    // Sample the texture with wave-distorted coordinates
    vec4 texColor = texture2D(u_texture, waveCoords);
    
    // Apply a color tint that changes over time
    vec3 tint = vec3(
        0.5 + 0.5 * sin(u_time * 2.0),
        0.5 + 0.5 * sin(u_time * 1.5 + 2.0),
        0.5 + 0.5 * sin(u_time * 1.0 + 4.0)
    );
    
    gl_FragColor = texColor * v_color * vec4(tint, 1.0);
}