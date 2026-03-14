package dev.creepysalhack.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Base class for all draggable HUD elements.
 * Each element has a position, size, and enabled state.
 */
public abstract class HudElement {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private float x, y;
    private boolean enabled = true;
    private boolean dragging = false;
    private float dragOffsetX, dragOffsetY;

    protected HudElement(String name, float defaultX, float defaultY) {
        this.name = name;
        this.x    = defaultX;
        this.y    = defaultY;
    }

    // ── Abstract ──────────────────────────────────────────────────────────────

    /** Render the element. Called every frame when enabled. */
    public abstract void render(DrawContext ctx, float delta);

    /** Width of this element in pixels (used by the HUD editor). */
    public abstract float getWidth();

    /** Height of this element in pixels. */
    public abstract float getHeight();

    // ── Drag support ──────────────────────────────────────────────────────────

    public boolean onMousePress(double mouseX, double mouseY) {
        if (mouseX >= x && mouseX <= x + getWidth()
         && mouseY >= y && mouseY <= y + getHeight()) {
            dragging     = true;
            dragOffsetX  = (float)(mouseX - x);
            dragOffsetY  = (float)(mouseY - y);
            return true;
        }
        return false;
    }

    public void onMouseDrag(double mouseX, double mouseY) {
        if (dragging) {
            x = (float)(mouseX - dragOffsetX);
            y = (float)(mouseY - dragOffsetY);
        }
    }

    public void onMouseRelease() { dragging = false; }

    // ── Getters/setters ───────────────────────────────────────────────────────

    public String  getName()           { return name; }
    public float   getX()              { return x; }
    public float   getY()              { return y; }
    public void    setX(float x)       { this.x = x; }
    public void    setY(float y)       { this.y = y; }
    public boolean isEnabled()         { return enabled; }
    public void    setEnabled(boolean e){ this.enabled = e; }
    public boolean isDragging()        { return dragging; }
}
