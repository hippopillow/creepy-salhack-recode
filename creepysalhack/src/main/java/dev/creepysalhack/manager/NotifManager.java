package dev.creepysalhack.manager;

import dev.creepysalhack.CreepySalHack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class NotifManager {

    public record Notification(String title, String message, long expireAt, Type type) {
        public enum Type { INFO, SUCCESS, WARNING, ERROR }
    }

    private final Deque<Notification> queue = new ArrayDeque<>();

    public void push(String title, String message, Notification.Type type) {
        long expire = System.currentTimeMillis() + 3500;
        queue.addLast(new Notification(title, message, expire, type));
        // keep queue bounded
        while (queue.size() > 5) queue.pollFirst();
    }

    public void info(String title, String msg)    { push(title, msg, Notification.Type.INFO); }
    public void success(String title, String msg) { push(title, msg, Notification.Type.SUCCESS); }
    public void warn(String title, String msg)    { push(title, msg, Notification.Type.WARNING); }
    public void error(String title, String msg)   { push(title, msg, Notification.Type.ERROR); }

    /** Tick – expire old notifications. Called from the tick mixin. */
    public void tick() {
        long now = System.currentTimeMillis();
        queue.removeIf(n -> now > n.expireAt());
    }

    public Deque<Notification> getActive() { return queue; }
}
