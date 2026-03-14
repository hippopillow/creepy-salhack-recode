package dev.creepysalhack.module.render;

import dev.creepysalhack.event.EventBus;
import dev.creepysalhack.event.Events;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;

public class FullbrightModule extends Module {

    private double originalGamma = 1.0;

    private final Setting<Float> gammaLevel = register(new Setting<>(
            "Gamma", "How bright to make the world", 16.0f, 1.0f, 32.0f));

    public FullbrightModule() {
        super("Fullbright", "Makes the world fully visible without light", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.options != null) {
            originalGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue((double) gammaLevel.getValue());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.options != null) {
            mc.options.getGamma().setValue(originalGamma);
        }
    }

    @EventBus.EventHandler
    public void onTick(Events.ClientTickEvent event) {
        if (event.getPhase() != Events.ClientTickEvent.Phase.PRE) return;
        if (mc.options != null) {
            mc.options.getGamma().setValue((double) gammaLevel.getValue());
        }
    }
}
