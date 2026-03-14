package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingElement extends HudElement {
    public PingElement() { super("Ping", 4, 110); }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        PlayerListEntry entry = mc.getNetworkHandler()
                .getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;

        int ping   = entry.getLatency();
        int colour = ping < 80  ? 0xFF00FF00 : ping < 150 ? 0xFFFFAA00 : 0xFFFF3333;
        String text = "Ping: " + ping + "ms";
        var tr = mc.textRenderer;
        int w = tr.getWidth(text) + 8;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), getX(), getY(), w,
                tr.fontHeight + 6, 3, RenderUtil.toARGB(10, 10, 10, 160));
        ctx.drawTextWithShadow(tr, text, (int) getX() + 4, (int) getY() + 3, colour);
    }

    @Override public float getWidth()  { return 80; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
