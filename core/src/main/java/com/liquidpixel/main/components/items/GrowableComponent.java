package com.liquidpixel.main.components.items;

import com.badlogic.ashley.core.Component;
import com.liquidpixel.main.engine.GameClock;
import com.liquidpixel.main.model.item.Growable;

import static com.liquidpixel.main.engine.GameClock.HOURS_PER_DAY;

public class GrowableComponent implements Component {

    // hours to grow
    int growthTime;

    long currentGrowthTime;

    long plantedTime;

    int yield;

    int[] steps = {2, 3, 4, 5, 6, 7};

    int currentStep = 4;

    public GrowableComponent(Growable growable) {
        this.growthTime = growable.getGrowthTime();
        this.yield = growable.getYield();
        this.plantedTime = GameClock.getTotalGameTimeMillis();
    }

    public int getGrowthTime() {
        return growthTime;
    }

    public long getCurrentGrowthTime() {
        return currentGrowthTime;
    }

    public void setCurrentGrowthTime(long currentGrowthTime) {
        this.currentGrowthTime = currentGrowthTime;
    }

    public long getPlantedTime() {
        return plantedTime;
    }

    public void setPlantedTime(long plantedTime) {
        this.plantedTime = plantedTime;
    }

    public int getYield() {
        return yield;
    }

    public void setYield(int yield) {
        this.yield = yield;
    }

    public int[] getSteps() {
        return steps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public int getHoursRemainingToGrow() {
        long currentGameTimeMillis = GameClock.getTotalGameTimeMillis();
        long requiredGrowthTimeMillis = GameClock.gameHoursToMillis(getGrowthTime());
        long elapsedTimeSincePlanting = currentGameTimeMillis - getPlantedTime();
        long remainingTimeMillis = requiredGrowthTimeMillis - elapsedTimeSincePlanting;
        if (remainingTimeMillis <= 0) {
            return 0;
        }

        return GameClock.millisToGameHours(remainingTimeMillis);
    }

    public GameClock.DayHour getTimeRemaining() {
        // Get current game time in milliseconds
        int totalHours = getHoursRemainingToGrow();
        int days = (int) (totalHours / HOURS_PER_DAY) + 1; // +1 because game starts at day 1
        int hours = (int) (totalHours % HOURS_PER_DAY);
        return new GameClock.DayHour(days, hours);
    }


}
