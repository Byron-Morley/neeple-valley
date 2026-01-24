package com.liquidpixel.main.components.render;

import com.badlogic.ashley.core.Component;

public class FadeComponent implements Component {
    private float alpha = 1.0f;
    private float targetAlpha = 1.0f;
    private float fadeSpeed = 1.0f;
    private boolean isFading = false;
    private FadeType fadeType = FadeType.NONE;

    public enum FadeType {
        NONE, FADE_IN, FADE_OUT
    }

    public void startFadeIn(float duration) {
        this.alpha = 0.0f;
        this.targetAlpha = 1.0f;
        this.fadeSpeed = 1.0f / duration;
        this.isFading = true;
        this.fadeType = FadeType.FADE_IN;
    }

    public void startFadeOut(float duration) {
        this.targetAlpha = 0.0f;
        this.fadeSpeed = 1.0f / duration;
        this.isFading = true;
        this.fadeType = FadeType.FADE_OUT;
    }

    public void update(float deltaTime) {
        if (!isFading) return;

        if (fadeType == FadeType.FADE_IN) {
            alpha += fadeSpeed * deltaTime;
            if (alpha >= targetAlpha) {
                alpha = targetAlpha;
                isFading = false;
                fadeType = FadeType.NONE;
            }
        } else if (fadeType == FadeType.FADE_OUT) {
            alpha -= fadeSpeed * deltaTime;
            if (alpha <= targetAlpha) {
                alpha = targetAlpha;
                isFading = false;
                fadeType = FadeType.NONE;
            }
        }
    }

    // Getters and setters
    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { this.alpha = alpha; }
    public boolean isFading() { return isFading; }
    public FadeType getFadeType() { return fadeType; }
}
