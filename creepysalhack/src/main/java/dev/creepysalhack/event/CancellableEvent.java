package dev.creepysalhack.event;

public abstract class CancellableEvent extends Event {
    private boolean cancelled = false;
    public void cancel()              { cancelled = true; }
    public boolean isCancelled()      { return cancelled; }
}
