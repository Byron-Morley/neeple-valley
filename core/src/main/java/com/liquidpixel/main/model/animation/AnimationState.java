package com.liquidpixel.main.model.animation;

import com.liquidpixel.core.core.Status;

import java.util.Objects;

public class AnimationState extends com.liquidpixel.sprite.model.AnimationState {
    public AnimationState(int currentFrame, int totalFrames, boolean isPlaying, Status currentAnimation, float stateTime) {
        super(currentFrame, totalFrames, isPlaying, currentAnimation, stateTime);
    }
}
