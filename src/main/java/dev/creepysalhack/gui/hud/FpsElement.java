package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class FpsElement extends HudElement {
    public FpsElement() { super("FPS", 4, 126); }

    @Override
    public void render(DrawContext ctx, float delta) {
        int fps    = mc.getCurrentFps();
        int colour = fps > 120 ? 0xFF00FF00 : fps > 60 ? 0xFFFFAA00 : 0xFFFF3333;
        String text = "FPS: " + fps;
        var tr = mc.textRenderer;
        int w = tr.getWidth(text) + 8;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), getX(), getY(), w,
                tr.fontHeight + 6, 3, RenderUtil.toARGB(10, 10, 10, 160));
        ctx.drawTextWithShadow(tr, text, (int) getX() + 4, (int) getY() + 3, colour);
    }

    @Override public float getWidth()  { return 70; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
