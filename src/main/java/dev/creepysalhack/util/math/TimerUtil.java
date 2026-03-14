package dev.creepysalhack.util.math;

public final class TimerUtil {

    private long lastMs = System.currentTimeMillis();
    private float timerSpeed = 1.0f;

    /** Returns true if {@code delay} ms have elapsed since last reset. */
    public boolean hasReached(float delay) {
        return System.currentTimeMillis() - lastMs >= delay / timerSpeed;
    }

    /** Reset the internal timestamp. */
    public void reset() { lastMs = System.currentTimeMillis(); }

    public void setSpeed(float speed) { timerSpeed = speed; }
    public float getSpeed() { return timerSpeed; }

    /** Returns elapsed ms since last reset. */
    public long elapsed() { return System.currentTimeMillis() - lastMs; }
}
