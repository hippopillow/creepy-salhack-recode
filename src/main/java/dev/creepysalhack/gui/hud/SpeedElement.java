package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.entity.PlayerUtil;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class SpeedElement extends HudElement {
    public SpeedElement() { super("Speed", 4, 158); }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        double speed = PlayerUtil.getHorizontalSpeed() * 20.0;
        String text  = String.format("Speed: §b%.2f§r bps", speed);
        var tr = mc.textRenderer;
        int w = tr.getWidth(text) + 8;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), getX(), getY(), w,
                tr.fontHeight + 6, 3, RenderUtil.toARGB(10, 10, 10, 160));
        ctx.drawTextWithShadow(tr, text, (int) getX() + 4, (int) getY() + 3, 0xFFFFFFFF);
    }

    @Override public float getWidth()  { return 100; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
