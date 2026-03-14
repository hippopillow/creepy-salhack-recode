package dev.creepysalhack.module;

import dev.creepysalhack.CreepySalHack;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every module in Creepy SalHack Recode.
 * Mirrors SalHack's Module but uses Fabric/1.21.4 APIs.
 */
public abstract class Module {

    // ── Category enum ────────────────────────────────────────────────────────
    public enum Category {
        COMBAT, MISC, RENDER, MOVEMENT, PLAYER, CLIENT
    }

    // ── Fields ────────────────────────────────────────────────────────────────
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String   name;
    private final String   description;
    private final Category category;
    private int            keyBind = -1;   // GLFW key code, -1 = unbound
    private boolean        enabled = false;
    private boolean        hidden  = false;

    private final List<Setting<?>> settings = new ArrayList<>();

    // Animation field used by arraylist HUD (mirrors SalHack's RemainingXAnimation)
    public float slideAnimation = 0f;

    // ── Constructor ───────────────────────────────────────────────────────────

    protected Module(String name, String description, Category category) {
        this.name        = name;
        this.description = description;
        this.category    = category;
    }

    protected Module(String name, String description, Category category, int keyBind) {
        this(name, description, category);
        this.keyBind = keyBind;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /** Called when the module is switched on. */
    public void onEnable()  {
        CreepySalHack.getEventManager().subscribe(this);
    }

    /** Called when the module is switched off. */
    public void onDisable() {
        CreepySalHack.getEventManager().unsubscribe(this);
    }

    /** Toggle the enabled state. */
    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else          onDisable();
        CreepySalHack.getConfigManager().save();
    }

    /** Force-set enabled without saving or firing events (used during config load). */
    public void setEnabledSilent(boolean v) { this.enabled = v; }

    // ── Settings registration ─────────────────────────────────────────────────

    protected <T> Setting<T> register(Setting<T> s) {
        settings.add(s);
        return s;
    }

    // ── Getters / setters ─────────────────────────────────────────────────────

    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public int      getKeyBind()     { return keyBind; }
    public void     setKeyBind(int k){ this.keyBind = k; }
    public boolean  isEnabled()      { return enabled; }
    public boolean  isHidden()       { return hidden; }
    public void     setHidden(boolean h) { this.hidden = h; }
    public List<Setting<?>> getSettings() { return settings; }

    /** Returns the suffix shown in the arraylist (e.g. mode name). Override to customise. */
    public String getArrayListSuffix() { return ""; }

    @Override
    public String toString() { return name; }
}
