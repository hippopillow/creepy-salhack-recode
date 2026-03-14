package dev.creepysalhack.gui.hud;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * HUD Editor – press the configured key (default: HOME) to open.
 * Renders all HUD elements with drag handles. Click to select, drag to reposition.
 */
public class HudEditorScreen extends Screen {

    private HudElement selected = null;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // dim background
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        RenderUtil.drawRect(ctx.getMatrices(), 0, 0, sw, sh,
                RenderUtil.toARGB(0, 0, 0, 100));

        var tr = client.textRenderer;
        String hint = "§7Drag HUD elements  |  ESC to save & close";
        ctx.drawTextWithShadow(tr, hint,
                sw / 2 - tr.getWidth(hint) / 2, sh - 14, 0xFF888899);

        List<HudElement> elements = CreepySalHack.getHudManager().getElements();

        for (HudElement el : elements) {
            // render element normally
            el.render(ctx, delta);

            // draw selection / hover outline
            boolean isSelected = el == selected;
            boolean hovered    = mouseX >= el.getX() && mouseX <= el.getX() + el.getWidth()
                              && mouseY >= el.getY() && mouseY <= el.getY() + el.getHeight();

            int outlineColour = isSelected
                    ? RenderUtil.getTimeRainbow(0.4f, 0.7f, 1f, 255)
                    : hovered
                        ? RenderUtil.toARGB(120, 120, 180, 200)
                        : RenderUtil.toARGB(60, 60, 80, 150);

            RenderUtil.drawRectOutline(ctx.getMatrices(),
                    el.getX() - 1, el.getY() - 1,
                    el.getWidth() + 2, el.getHeight() + 2,
                    1, outlineColour);

            // name tag above element
            ctx.drawTextWithShadow(tr, "§b" + el.getName(),
                    (int)el.getX(), (int)el.getY() - tr.fontHeight - 1,
                    0xFFCCCCDD);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        selected = null;
        for (HudElement el : CreepySalHack.getHudManager().getElements()) {
            if (el.onMousePress(mx, my)) {
                selected = el;
                break;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (selected != null) selected.onMouseDrag(mx, my);
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (selected != null) selected.onMouseRelease();
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() {
        CreepySalHack.getConfigManager().save();
        super.close();
    }
}
