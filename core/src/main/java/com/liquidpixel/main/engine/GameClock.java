package com.liquidpixel.main.engine;

public class GameClock {
    private static int days = 1;
    private static float secondsCounter = 0;
    public static final float SECONDS_PER_DAY = 600.0f;
    public static final float HOURS_PER_DAY = 24.0f;
    public static final float SECONDS_PER_HOUR = SECONDS_PER_DAY / HOURS_PER_DAY; // 25 seconds per game hour

    public record DayHour(int days, int hours) {

    }

    public static void update(float deltaTime) {
        if (!GameState.isPaused()) {
            secondsCounter += deltaTime;
            if (secondsCounter >= SECONDS_PER_DAY) {
                days++;
                secondsCounter -= SECONDS_PER_DAY; // Reset but keep remainder
            }
        }
    }

    public static int getDays() {
        return days;
    }

    public static void setDays(int newDays) {
        days = newDays;
    }

    public static String getTimeString() {
        return String.format("Day %d, %02d:00", days, getHours());
    }

    // Returns the current hour (0-23)

    public static int getHours() {
        return (int) (secondsCounter / SECONDS_PER_HOUR);
    }
    // Returns total elapsed time in hours since game start

    public static float getTotalHours() {
        return ((days - 1) * HOURS_PER_DAY) + (secondsCounter / SECONDS_PER_HOUR);
    }
    // Returns a value between 0.0 and 1.0 indicating progress to the next day

    public static float getDayProgress() {
        return secondsCounter / SECONDS_PER_DAY;
    }
    // Returns a value between 0.0 and 1.0 indicating progress to the next hour

    public static float getHourProgress() {
        return (secondsCounter % SECONDS_PER_HOUR) / SECONDS_PER_HOUR;
    }
    // Get total game time in milliseconds since start

    public static long getTotalGameTimeMillis() {
        float totalSeconds = (days - 1) * SECONDS_PER_DAY + secondsCounter;
        return (long) (totalSeconds * 1000);
    }

    public static int millisToGameHours(long remainingTimeMillis) {
        // Convert milliseconds to seconds, then to game hours
        float seconds = remainingTimeMillis / 1000.0f;
        float hours = seconds / SECONDS_PER_HOUR;
        return (int) Math.ceil(hours);
    }


    // Convert game hours to milliseconds
    public static long gameHoursToMillis(int growthTime) {
        return (long) (growthTime * SECONDS_PER_HOUR * 1000);
    }

    // Get a more detailed time string with minutes
    public static String getDetailedTimeString() {
        int hours = getHours();
        int minutes = (int) ((secondsCounter % SECONDS_PER_HOUR) / SECONDS_PER_HOUR * 60);
        return String.format("Day %d, %02d:%02d", days, hours, minutes);
    }

    // Reset the clock to initial state
    public static void reset() {
        days = 1;
        secondsCounter = 0;
    }

    public static DayHour convertHoursToDayHour(int totalHours) {
        int days = (int) (totalHours / HOURS_PER_DAY) + 1; // +1 because game starts at day 1
        int hours = (int) (totalHours % HOURS_PER_DAY);
        return new DayHour(days, hours);
    }
}
