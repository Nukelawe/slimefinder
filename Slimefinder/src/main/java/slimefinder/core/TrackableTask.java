package slimefinder.core;

import slimefinder.io.CLI;

import java.time.Duration;
import java.time.Instant;

public abstract class TrackableTask implements Runnable {
    protected Instant startTime;
    protected Duration duration;
    protected boolean isFinished;
    protected boolean isInterrupted;

    public void setStartTime() {
        startTime = Instant.now();
    }

    public void stop() {
        duration = Duration.between(startTime, Instant.now());
        Thread.currentThread().interrupt();
    }

    /**
     * @return duration of the task since it was started in nanoseconds.
     */
    protected long getDuration() {
        if (startTime == null) return 0;
        if (duration != null) return duration.toNanos();
        return Duration.between(startTime, Instant.now()).toNanos();
    }

    public synchronized void interrupt(CLI cli) {
        cli.info("Task interrupted");
        isInterrupted = true;
    }

    public abstract String startInfo();

    public abstract String progressInfo();

    public abstract String endInfo();
}
