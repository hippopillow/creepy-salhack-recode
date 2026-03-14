package dev.creepysalhack.manager;

import dev.creepysalhack.gui.hud.*;

import java.util.ArrayList;
import java.util.List;

public class HudManager {

    private final List<HudElement> elements = new ArrayList<>();

    public HudManager() {
        register(new WatermarkElement());
        register(new ArrayListElement());
        register(new CoordinatesElement());
        register(new ArmorElement());
        register(new PingElement());
        register(new FpsElement());
        register(new TotemCountElement());
        register(new SpeedElement());
        register(new NotificationElement());
    }

    private void register(HudElement e) { elements.add(e); }

    public List<HudElement> getElements() { return elements; }
}
