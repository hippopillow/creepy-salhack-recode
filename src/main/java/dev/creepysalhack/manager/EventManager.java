package dev.creepysalhack.manager;

import dev.creepysalhack.event.Event;
import dev.creepysalhack.event.EventBus;

public class EventManager {
    private final EventBus bus = new EventBus();

    public void subscribe(Object o)     { bus.subscribe(o); }
    public void unsubscribe(Object o)   { bus.unsubscribe(o); }
    public <E extends Event> E post(E e){ return bus.post(e); }
}
