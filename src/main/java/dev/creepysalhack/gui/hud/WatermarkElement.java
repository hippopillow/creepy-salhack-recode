package dev.creepysalhack.gui.hud;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class WatermarkElement extends HudElement {

    private static final String TEXT    = "Creepy SalHack";
    private static final String VERSION = " v" + CreepySalHack.VERSION;

    public WatermarkElement() { super("Watermark", 4, 4); }

    @Override
    public void render(DrawContext ctx, float delta) {
        var tr   = mc.textRenderer;
        float x  = getX();
        float y  = getY();
        int  w   = tr.getWidth(TEXT + VERSION) + 8;
        int  h   = tr.fontHeight + 6;

        // dark background
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, w, h, 3,
                RenderUtil.toARGB(15, 15, 15, 180));

        // rainbow-coloured client name
        int rainbow = RenderUtil.getTimeRainbow(0.5f, 0.7f, 1.0f, 255);
        ctx.drawTextWithShadow(tr, TEXT, (int)(x + 4), (int)(y + 4), rainbow);

        // version in grey
        ctx.drawTextWithShadow(tr, VERSION,
                (int)(x + 4 + tr.getWidth(TEXT)), (int)(y + 4), 0xFFAAAAAA);
    }

    @Override public float getWidth()  { return mc.textRenderer.getWidth(TEXT + VERSION) + 8; }
    @Override public float getHeight() { return mc.textRenderer.fontHeight + 6; }
}
