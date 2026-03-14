package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.entity.PlayerUtil;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TotemCountElement extends HudElement {
    public TotemCountElement() { super("TotemCount", 4, 142); }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        int totems = PlayerUtil.countTotems();
        int colour = totems > 4 ? 0xFF00FF00 : totems > 1 ? 0xFFFFAA00 : 0xFFFF3333;
        String text = "Totems: " + totems;
        var tr = mc.textRenderer;
        int w = tr.getWidth(text) + 8;
        RenderUtil.drawRoundedRect(ctx.getMatrices(), getX(), getY(), w,
                tr.fontHeight + 6, 3, RenderUtil.toARGB(10, 10, 10, 160));
        ctx.drawTextWithShadow(tr, text, (int) getX() + 4, (int) getY() + 3, colour);
    }

    @Override public float getWidth()  { return 90; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
