package dev.creepysalhack.gui.hud;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArrayListElement extends HudElement {

    private static final float ANIM_SPEED = 0.15f;
    private static final int   LINE_H     = 12;
    private static final int   PADDING    = 3;

    public ArrayListElement() {
        // anchor top-right; actual X is computed per-frame
        super("ArrayList", 0, 4);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        if (mc.currentScreen != null) return;         // hide while GUI open

        var tr   = mc.textRenderer;
        int sw   = mc.getWindow().getScaledWidth();

        // collect enabled, non-hidden modules sorted by display-name width desc
        List<Module> active = new ArrayList<>();
        for (Module m : CreepySalHack.getModuleManager().getModules()) {
            if (m.isEnabled() && !m.isHidden()) active.add(m);
        }
        active.sort(Comparator.comparingInt(
                m -> -(tr.getWidth(getLabel(m)))));

        float drawY = getY();
        for (Module m : active) {
            String label  = getLabel(m);
            float  target = tr.getWidth(label) + PADDING * 2 + 4f;

            // slide animation (mirrors SalHack RemainingXAnimation)
            if (m.slideAnimation < target) {
                m.slideAnimation += (target - m.slideAnimation) * ANIM_SPEED
                                    + ANIM_SPEED;
            } else {
                m.slideAnimation = target;
            }

            float barW = m.slideAnimation;
            float barX = sw - barW;

            int rainbow = RenderUtil.getTimeRainbow(0.4f, 0.65f, 1.0f, 255);

            // dark bg
            RenderUtil.drawRect(ctx.getMatrices(),
                    barX, drawY, barW, LINE_H,
                    RenderUtil.toARGB(10, 10, 10, 160));

            // left accent bar
            RenderUtil.drawRect(ctx.getMatrices(),
                    barX, drawY, 2, LINE_H, rainbow);

            // label
            ctx.drawTextWithShadow(tr, label,
                    (int)(barX + PADDING + 2), (int)(drawY + 2), 0xFFFFFFFF);

            drawY += LINE_H + 1;
        }

        // slide-out for modules that were just disabled
        for (Module m : CreepySalHack.getModuleManager().getModules()) {
            if (!m.isEnabled() && m.slideAnimation > 0) {
                m.slideAnimation -= m.slideAnimation * ANIM_SPEED + ANIM_SPEED;
                if (m.slideAnimation < 0) m.slideAnimation = 0;
            }
        }
    }

    private String getLabel(Module m) {
        String suffix = m.getArrayListSuffix();
        return suffix.isEmpty() ? m.getName() : m.getName() + " §7" + suffix;
    }

    @Override public float getWidth()  { return 120; }
    @Override public float getHeight() {
        long count = CreepySalHack.getModuleManager().getModules()
                .stream().filter(Module::isEnabled).count();
        return count * (LINE_H + 1);
    }
}
