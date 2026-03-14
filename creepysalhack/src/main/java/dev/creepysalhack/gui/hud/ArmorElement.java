package dev.creepysalhack.gui.hud;

import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorElement extends HudElement {

    private static final int SLOT_SIZE = 18;
    private static final int SLOTS     = 4;

    public ArmorElement() { super("Armor", 4, 60); }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.player == null) return;
        var tr  = mc.textRenderer;
        float x = getX(), y = getY();
        int   w = SLOTS * SLOT_SIZE + 6;
        int   h = SLOT_SIZE + tr.fontHeight + 8;

        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, w, h, 3,
                RenderUtil.toARGB(10, 10, 10, 160));

        // Armor slots: head=3, chest=2, legs=1, feet=0
        int[] armorSlots = {3, 2, 1, 0};
        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack stack = mc.player.getInventory().getArmorStack(armorSlots[i]);
            if (stack.isEmpty()) continue;

            float ix = x + 3 + i * SLOT_SIZE;
            float iy = y + 3;

            ctx.drawItem(stack, (int)ix, (int)iy);
            ctx.drawItemInSlot(tr, stack, (int)ix, (int)iy);

            // durability bar colour
            if (stack.isDamageable()) {
                float pct = 1f - (float)stack.getDamage() / stack.getMaxDamage();
                int colour = pct > 0.5f ? 0xFF00FF00
                           : pct > 0.25f ? 0xFFFFAA00
                           : 0xFFFF3333;
                String durStr = String.valueOf((int)(pct * 100));
                ctx.drawTextWithShadow(tr, durStr,
                        (int)(ix + SLOT_SIZE/2f - tr.getWidth(durStr)/2f),
                        (int)(iy + SLOT_SIZE + 1), colour);
            }
        }
    }

    @Override public float getWidth()  { return SLOTS * SLOT_SIZE + 6; }
    @Override public float getHeight() { return SLOT_SIZE + mc.textRenderer.fontHeight + 8; }
}
