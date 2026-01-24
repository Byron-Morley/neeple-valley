// Modified outline.frag
#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_outlineWidth;
uniform vec4 u_outlineColor;
uniform vec2 u_textureSize;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord);

    // Check if current pixel is part of the sprite
    bool isSprite = texColor.a > 0.1;

    // If this is a sprite pixel, just output the sprite color
    if (isSprite) {
        gl_FragColor = texColor * v_color;
        return;
    }

    // For transparent pixels, check if they should be part of the outline
    vec2 pixelSize = 1.0 / u_textureSize;

    // Check neighboring pixels to see if we're near the sprite
    bool isOutline = false;
    float minDistance = u_outlineWidth + 1.0;

    for (float y = -u_outlineWidth; y <= u_outlineWidth; y += 1.0) {
        for (float x = -u_outlineWidth; x <= u_outlineWidth; x += 1.0) {
            // Skip checking the current pixel
            if (x == 0.0 && y == 0.0) continue;

            // Calculate the distance from current pixel
            float dist = sqrt(x*x + y*y);

            // Skip if outside outline width
            if (dist > u_outlineWidth) continue;

            // Sample the neighboring pixel
            vec2 sampleCoord = v_texCoord + vec2(x, y) * pixelSize;
            float sampleAlpha = texture2D(u_texture, sampleCoord).a;

            // If the sample is part of the sprite and closer than our current minimum
            if (sampleAlpha > 0.1 && dist < minDistance) {
                isOutline = true;
                minDistance = dist;
            }
        }
    }

    // If we're part of the outline, use outline color
    if (isOutline) {
        // Calculate opacity based on distance (farther = more transparent)
        float opacity = 1.0 - (minDistance / u_outlineWidth);
        // Make the opacity curve steeper for a sharper outline
        opacity = pow(opacity, 0.5);
        gl_FragColor = vec4(u_outlineColor.rgb, u_outlineColor.a * opacity);
    } else {
        // Otherwise, use the texture color (which should be transparent)
        gl_FragColor = texColor * v_color;
    }
}
