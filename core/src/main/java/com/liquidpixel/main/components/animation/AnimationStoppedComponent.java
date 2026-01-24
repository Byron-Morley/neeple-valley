package com.liquidpixel.main.components.animation;

import com.badlogic.ashley.core.Component;

/**
 * Component to track if an entity's animation has been manually stopped.
 * When this component is present, the animation should not auto-advance.
 */
public class AnimationStoppedComponent implements Component {
    // Marker component - presence indicates animation is stopped
}
