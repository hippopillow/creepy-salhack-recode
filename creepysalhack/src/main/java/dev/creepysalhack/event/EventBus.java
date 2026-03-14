package dev.creepysalhack.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Minimal, zero-dependency event bus.
 * Subscribers annotate methods with @EventHandler and call bus.subscribe(this).
 */
public class EventBus {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface EventHandler {}

    private record Registration(Object subscriber, Method method) {}

    private final Map<Class<?>, List<Registration>> registry = new ConcurrentHashMap<>();

    /** Register all @EventHandler methods on the given object. */
    public void subscribe(Object subscriber) {
        for (Method m : subscriber.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(EventHandler.class)) continue;
            if (m.getParameterCount() != 1) continue;
            Class<?> eventType = m.getParameterTypes()[0];
            m.setAccessible(true);
            registry.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                    .add(new Registration(subscriber, m));
        }
    }

    /** Remove all handlers belonging to subscriber. */
    public void unsubscribe(Object subscriber) {
        for (List<Registration> list : registry.values()) {
            list.removeIf(r -> r.subscriber() == subscriber);
        }
    }

    /** Dispatch an event to all registered handlers. Returns the event. */
    public <E extends Event> E post(E event) {
        List<Registration> handlers = registry.get(event.getClass());
        if (handlers == null) return event;
        for (Registration r : handlers) {
            try {
                r.method().invoke(r.subscriber(), event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (event instanceof CancellableEvent ce && ce.isCancelled()) break;
        }
        return event;
    }

    /** True when at least one handler is registered for the given event class. */
    public boolean hasHandlers(Class<?> eventClass) {
        List<Registration> list = registry.get(eventClass);
        return list != null && !list.isEmpty();
    }
}
