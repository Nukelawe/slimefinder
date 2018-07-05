package slimefinder.core;

import java.time.Duration;
import java.time.Instant;

public abstract class TrackableTask implements Runnable {
    protected Instant startTime;
    protected Duration duration;
    protected boolean isFinished;

    public void setStartTime() {
        startTime = Instant.now();
    }

    public void stop() {
        duration = Duration.between(startTime, Instant.now());
        Thread.currentThread().interrupt();
        isFinished = true;
    }

    /**
     * @return duration of the task since it was started in nanoseconds.
     */
    protected synchronized long getDuration() {
        if (startTime == null) return 0;
        if (duration != null) return duration.toNanos();
        return Duration.between(startTime, Instant.now()).toNanos();
    }

    public abstract String startInfo();

    public abstract String progressInfo();

    public abstract String endInfo();
}
