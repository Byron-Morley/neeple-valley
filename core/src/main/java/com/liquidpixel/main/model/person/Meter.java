package com.liquidpixel.main.model.person;

import static com.liquidpixel.main.engine.GameClock.SECONDS_PER_DAY;

public class Meter {
    private final String name;
    private final float maxValue;
    private final float threshold;
    private final float decreasePerSecond;

    private float currentValue;
    private float accumulatedDelta;
    private boolean thresholdReached;

    /**
     * Creates a new meter with specified parameters.
     *
     * @param name The name of the meter (e.g., "Food", "Sleep")
     * @param maxValue The maximum value the meter can have
     * @param startValue The initial value of the meter
     * @param threshold The lower threshold that triggers an alert when reached
     * @param decreasePerDay How much the meter decreases per day (in game time)
     */
    public Meter(String name, float maxValue, float startValue, float threshold, float decreasePerDay) {
        this.name = name;
        this.maxValue = maxValue;
        this.threshold = threshold;
        this.currentValue = Math.min(startValue, maxValue);
        this.decreasePerSecond = decreasePerDay / SECONDS_PER_DAY;
        this.thresholdReached = currentValue <= threshold;
        this.accumulatedDelta = 0f;
    }

    /**
     * Updates the meter based on elapsed time.
     *
     * @param delta The time elapsed since the last update in seconds
     * @return true if at least 1 unit was decreased, false otherwise
     */
    public boolean tick(float delta) {
        if (currentValue <= 0) {
            return false;
        }

        float previousValue = currentValue;
        accumulatedDelta += delta;

        // Calculate how much to decrease
        float decrease = accumulatedDelta * decreasePerSecond;

        // Only update if we're decreasing by at least 1 unit
        if (decrease >= 1.0f) {
            currentValue -= decrease;
            accumulatedDelta = 0f;

            // Ensure we don't go below zero
            if (currentValue < 0) {
                currentValue = 0;
            }

            // Check if we've crossed the threshold
            if (previousValue > threshold && currentValue <= threshold) {
                thresholdReached = true;
            }

            return true;
        }

        return false;
    }

    /**
     * Checks if the meter has reached or gone below the threshold.
     *
     * @return true if the threshold has been reached, false otherwise
     */
    public boolean isThresholdReached() {
        return thresholdReached;
    }

    /**
     * Resets the threshold reached flag after it's been handled.
     */
    public void acknowledgeThreshold() {
        thresholdReached = false;
    }

    /**
     * Adds the specified amount to the meter, not exceeding the maximum value.
     *
     * @param amount The amount to add
     */
    public void add(float amount) {
        currentValue = Math.min(currentValue + amount, maxValue);

        // If we were below threshold and now we're above it, update the flag
        if (thresholdReached && currentValue > threshold) {
            thresholdReached = false;
        }
    }

    /**
     * Subtracts the specified amount from the meter, not going below zero.
     *
     * @param amount The amount to subtract
     */
    public void subtract(float amount) {
        float previousValue = currentValue;
        currentValue = Math.max(currentValue - amount, 0);

        // Check if we've crossed the threshold
        if (previousValue > threshold && currentValue <= threshold) {
            thresholdReached = true;
        }
    }

    /**
     * Sets the meter to the specified value, constrained between 0 and max.
     *
     * @param value The value to set
     */
    public void setValue(float value) {
        float previousValue = currentValue;
        currentValue = Math.max(0, Math.min(value, maxValue));

        // Check if we've crossed the threshold
        if (previousValue > threshold && currentValue <= threshold) {
            thresholdReached = true;
        } else if (previousValue <= threshold && currentValue > threshold) {
            thresholdReached = false;
        }
    }

    /**
     * Gets the current value of the meter.
     *
     * @return The current value
     */
    public float getCurrentValue() {
        return currentValue;
    }

    /**
     * Gets the maximum value of the meter.
     *
     * @return The maximum value
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * Gets the threshold value of the meter.
     *
     * @return The threshold value
     */
    public float getThreshold() {
        return threshold;
    }

    /**
     * Gets the name of the meter.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the percentage of the meter (current value / max value).
     *
     * @return The percentage as a value between 0.0 and 1.0
     */
    public float getPercentage() {
        return currentValue / maxValue;
    }
}
