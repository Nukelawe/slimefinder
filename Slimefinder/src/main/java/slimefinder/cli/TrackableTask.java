package slimefinder.cli;


import java.time.Duration;
import java.time.Instant;

public abstract class TrackableTask implements Runnable {
    protected Instant startTime;
    protected Duration duration;

    public void setStartTime() {
        startTime = Instant.now();
    }

    public void stop() {
        duration = Duration.between(startTime, Instant.now());
        Thread.currentThread().interrupt();
    }

    /**
     * @return progress of this task as a long.
     */
    public abstract long getProgress();

    /**
     * @return progress when the task is complete.
     */
    public abstract long getMaxProgress();

    /**
     * @return duration of the task since it was started in nanoseconds.
     */
    public synchronized long getDuration() {
        if (duration != null) return duration.getNano();
        return Duration.between(startTime, Instant.now()).getNano();
    }

    /**
     *
     */
    public abstract String getProgressInfo();
}
