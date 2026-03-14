package dev.creepysalhack.gui.hud;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.manager.NotifManager.Notification;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class NotificationElement extends HudElement {

    private static final int NOTIF_W  = 200;
    private static final int NOTIF_H  = 36;
    private static final int PADDING  = 4;
    private static final int MARGIN   = 4;

    public NotificationElement() { super("Notifications", 0, 0); }

    @Override
    public void render(DrawContext ctx, float delta) {
        var tr  = mc.textRenderer;
        int sw  = mc.getWindow().getScaledWidth();
        int sh  = mc.getWindow().getScaledHeight();

        List<Notification> notifs = new ArrayList<>(
                CreepySalHack.getNotifManager().getActive());

        float drawY = sh - MARGIN;
        for (int i = notifs.size() - 1; i >= 0; i--) {
            Notification n = notifs.get(i);
            drawY -= NOTIF_H + MARGIN;

            float x = sw - NOTIF_W - MARGIN;
            float y = drawY;

            // Background
            RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, NOTIF_W, NOTIF_H,
                    4, RenderUtil.toARGB(20, 20, 20, 210));

            // Accent stripe colour based on type
            int accent = switch (n.type()) {
                case SUCCESS -> RenderUtil.toARGB(0,   200, 80,  255);
                case WARNING -> RenderUtil.toARGB(255, 170, 0,   255);
                case ERROR   -> RenderUtil.toARGB(220, 50,  50,  255);
                default      -> RenderUtil.toARGB(80,  140, 255, 255);
            };
            RenderUtil.drawRect(ctx.getMatrices(), x, y, 3, NOTIF_H, accent);

            // Title
            ctx.drawTextWithShadow(tr, "§l" + n.title(),
                    (int)(x + PADDING + 5), (int)(y + PADDING + 1), 0xFFFFFFFF);

            // Message (truncated)
            String msg = n.message();
            if (tr.getWidth(msg) > NOTIF_W - 16) {
                while (tr.getWidth(msg + "...") > NOTIF_W - 16 && !msg.isEmpty())
                    msg = msg.substring(0, msg.length() - 1);
                msg += "...";
            }
            ctx.drawTextWithShadow(tr, msg,
                    (int)(x + PADDING + 5), (int)(y + PADDING + 2 + tr.fontHeight + 2),
                    0xFFAAAAAA);

            // Progress bar (time remaining)
            long now    = System.currentTimeMillis();
            float pct   = Math.max(0, (n.expireAt() - now) / 3500f);
            RenderUtil.drawRect(ctx.getMatrices(),
                    x + 3, y + NOTIF_H - 3, (NOTIF_W - 3) * pct, 2, accent);
        }
    }

    @Override public float getWidth()  { return NOTIF_W; }
    @Override public float getHeight() { return 0; }
}
