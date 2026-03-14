package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class CoordinatesElement extends HudElement {

    public CoordinatesElement() { super("Coordinates", 4, 20); }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        var tr = mc.textRenderer;
        float x = getX(), y = getY();

        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        String xyzLine = String.format("XYZ: §b%.1f §f/ §b%.1f §f/ §b%.1f", px, py, pz);

        // Nether / overworld coordinate toggle
        boolean isNether = mc.world != null &&
                mc.world.getDimensionKey().getValue().getPath().contains("nether");
        String altLine = isNether
                ? String.format("OW: §a%.1f §f/ §f/ §a%.1f", px * 8, pz * 8)
                : String.format("Nether: §c%.1f §f/ §f/ §c%.1f", px / 8, pz / 8);

        int w = Math.max(tr.getWidth(xyzLine), tr.getWidth(altLine)) + 8;
        int h = tr.fontHeight * 2 + 8;

        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, w, h, 3,
                RenderUtil.toARGB(10, 10, 10, 160));

        ctx.drawTextWithShadow(tr, xyzLine, (int)(x+4), (int)(y+3), 0xFFFFFFFF);
        ctx.drawTextWithShadow(tr, altLine,  (int)(x+4), (int)(y+3+tr.fontHeight+2), 0xFFFFFFFF);
    }

    @Override public float getWidth()  { return 180; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight * 2 + 8; }
}
