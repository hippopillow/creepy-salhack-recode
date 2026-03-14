package dev.creepysalhack.gui.click;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Module.Category;
import dev.creepysalhack.module.Setting;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/**
 * A single ClickGUI panel for one module category.
 * Supports drag, collapse, module toggle, and per-module setting expansion.
 * Styled after the original Creepy SalHack panel aesthetic.
 */
public class CategoryPanel {

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final int PANEL_W       = 110;
    private static final int HEADER_H      = 18;
    private static final int MODULE_H      = 14;
    private static final int SETTING_H     = 13;
    private static final int PADDING       = 4;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final int COL_HEADER_BG = RenderUtil.toARGB(25, 25, 35,  230);
    private static final int COL_PANEL_BG  = RenderUtil.toARGB(15, 15, 22,  210);
    private static final int COL_MOD_ON    = RenderUtil.toARGB(40, 40, 60,  200);
    private static final int COL_MOD_OFF   = RenderUtil.toARGB(20, 20, 30,  180);
    private static final int COL_SETTING   = RenderUtil.toARGB(12, 12, 20,  200);
    private static final int COL_HOVER     = RenderUtil.toARGB(60, 60, 90,  180);
    private static final int COL_TEXT      = 0xFFE0E0FF;
    private static final int COL_TEXT_DIM  = 0xFF888899;

    // ── State ─────────────────────────────────────────────────────────────────
    private float x, y;
    private boolean collapsed  = false;
    private boolean dragging   = false;
    private float   dragDX, dragDY;

    private final Category      category;
    private final String        title;
    private final List<Module>  modules;

    /** The module whose settings are currently expanded; null = none. */
    private Module expandedModule = null;

    // ── Constructor ───────────────────────────────────────────────────────────

    public CategoryPanel(Category category, String title, float x, float y) {
        this.category = category;
        this.title    = title;
        this.x        = x;
        this.y        = y;
        this.modules  = CreepySalHack.getModuleManager().getByCategory(category);
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public void render(DrawContext ctx, int mouseX, int mouseY) {
        var tr = MinecraftClient.getInstance().textRenderer;

        // ── Header ─────────────────────────────────────────────────────────
        RenderUtil.drawRoundedRect(ctx.getMatrices(), x, y, PANEL_W, HEADER_H, 4,
                COL_HEADER_BG);

        int rainbow = RenderUtil.getTimeRainbow(0.35f, 0.6f, 1.0f, 255);

        // accent stripe along top of header
        RenderUtil.drawRect(ctx.getMatrices(), x + 4, y, PANEL_W - 8, 2, rainbow);

        // collapse arrow
        String arrow = collapsed ? "▶" : "▼";
        ctx.drawTextWithShadow(tr, arrow,
                (int)(x + PANEL_W - 12), (int)(y + HEADER_H/2f - tr.fontHeight/2f),
                COL_TEXT_DIM);

        // category name
        ctx.drawTextWithShadow(tr, title,
                (int)(x + PADDING + 2), (int)(y + HEADER_H/2f - tr.fontHeight/2f),
                COL_TEXT);

        if (collapsed) return;

        // ── Module list ───────────────────────────────────────────────────
        float iy = y + HEADER_H + 1;

        for (Module m : modules) {
            boolean hovered = isHovered(mouseX, mouseY, x, iy, PANEL_W, MODULE_H);
            int bg = m.isEnabled() ? COL_MOD_ON : (hovered ? COL_HOVER : COL_MOD_OFF);

            RenderUtil.drawRect(ctx.getMatrices(), x, iy, PANEL_W, MODULE_H, bg);

            // enabled indicator bar on left
            if (m.isEnabled()) {
                RenderUtil.drawRect(ctx.getMatrices(), x, iy, 2, MODULE_H, rainbow);
            }

            // module name
            int textColour = m.isEnabled() ? 0xFFFFFFFF : COL_TEXT_DIM;
            ctx.drawTextWithShadow(tr, m.getName(),
                    (int)(x + PADDING + 3), (int)(iy + MODULE_H/2f - tr.fontHeight/2f),
                    textColour);

            // suffix (e.g. mode name)
            String suffix = m.getArrayListSuffix();
            if (!suffix.isEmpty()) {
                ctx.drawTextWithShadow(tr, "§7" + suffix,
                        (int)(x + PANEL_W - tr.getWidth(suffix) - PADDING),
                        (int)(iy + MODULE_H/2f - tr.fontHeight/2f), COL_TEXT_DIM);
            }

            // expand arrow if module has settings
            if (!m.getSettings().isEmpty()) {
                String expArrow = (expandedModule == m) ? "▲" : "▼";
                ctx.drawTextWithShadow(tr, "§8" + expArrow,
                        (int)(x + PANEL_W - 10),
                        (int)(iy + MODULE_H/2f - tr.fontHeight/2f), COL_TEXT_DIM);
            }

            iy += MODULE_H;

            // ── Settings expansion ────────────────────────────────────────
            if (expandedModule == m) {
                for (Setting<?> s : m.getSettings()) {
                    RenderUtil.drawRect(ctx.getMatrices(), x + 2, iy,
                            PANEL_W - 2, SETTING_H, COL_SETTING);

                    String valStr = formatValue(s);
                    boolean sHovered = isHovered(mouseX, mouseY, x+2, iy, PANEL_W-2, SETTING_H);

                    if (sHovered) {
                        RenderUtil.drawRect(ctx.getMatrices(), x+2, iy,
                                PANEL_W-2, SETTING_H,
                                RenderUtil.toARGB(50,50,80,120));
                    }

                    // setting name
                    ctx.drawTextWithShadow(tr, "§7" + s.getName(),
                            (int)(x + PADDING + 5),
                            (int)(iy + SETTING_H/2f - tr.fontHeight/2f),
                            COL_TEXT_DIM);

                    // setting value on right
                    int valColour = s.isBoolean()
                            ? ((Boolean)s.getValue() ? 0xFF00FF88 : 0xFFFF4444)
                            : 0xFF88CCFF;
                    ctx.drawTextWithShadow(tr, valStr,
                            (int)(x + PANEL_W - tr.getWidth(valStr) - PADDING),
                            (int)(iy + SETTING_H/2f - tr.fontHeight/2f),
                            valColour);

                    // slider bar for numeric settings
                    if (s.isNumber() && s.getMin() != null && s.getMax() != null) {
                        double cur = ((Number)s.getValue()).doubleValue();
                        double mn  = ((Number)s.getMin()).doubleValue();
                        double mx  = ((Number)s.getMax()).doubleValue();
                        float pct  = (float)((cur - mn) / (mx - mn));
                        float barW = (PANEL_W - 8) * pct;
                        RenderUtil.drawRect(ctx.getMatrices(),
                                x + 4, iy + SETTING_H - 2, barW, 2,
                                RenderUtil.toARGB(80, 140, 255, 200));
                    }

                    iy += SETTING_H;
                }
            }
        }

        // panel background fill behind modules
        float totalH = iy - (y + HEADER_H + 1);
        if (totalH > 0) {
            // draw behind – already drawn per-row above; just add a border
            RenderUtil.drawRectOutline(ctx.getMatrices(), x, y + HEADER_H,
                    PANEL_W, totalH + 1, 1,
                    RenderUtil.toARGB(40, 40, 60, 180));
        }
    }

    // ── Mouse handling ────────────────────────────────────────────────────────

    /** Returns true if the click was consumed. */
    public boolean mouseClicked(double mx, double my, int button) {
        // Header – drag or collapse
        if (isHovered((int)mx,(int)my, x, y, PANEL_W, HEADER_H)) {
            if (button == 0) {
                dragging = true;
                dragDX   = (float)(mx - x);
                dragDY   = (float)(my - y);
            } else if (button == 1) {
                collapsed = !collapsed;
            }
            return true;
        }

        if (collapsed) return false;

        float iy = y + HEADER_H + 1;
        for (Module m : modules) {
            if (isHovered((int)mx,(int)my, x, iy, PANEL_W, MODULE_H)) {
                if (button == 0) {
                    // left-click = toggle
                    m.toggle();
                    playClick();
                } else if (button == 1) {
                    // right-click = expand settings
                    expandedModule = (expandedModule == m) ? null : m;
                }
                return true;
            }
            iy += MODULE_H;

            if (expandedModule == m) {
                for (Setting<?> s : m.getSettings()) {
                    if (isHovered((int)mx,(int)my, x+2, iy, PANEL_W-2, SETTING_H)) {
                        handleSettingClick(s, button, (float)mx, iy);
                        return true;
                    }
                    iy += SETTING_H;
                }
            }
        }
        return false;
    }

    public void mouseDragged(double mx, double my) {
        if (dragging) {
            x = (float)(mx - dragDX);
            y = (float)(my - dragDY);
        }
    }

    public void mouseReleased() { dragging = false; }

    /** Called when the mouse scroll wheel moves over this panel. */
    public void mouseScrolled(double mx, double my, double amount) {
        if (collapsed) return;
        float iy = y + HEADER_H + 1;
        for (Module m : modules) {
            iy += MODULE_H;
            if (expandedModule == m) {
                for (Setting<?> s : m.getSettings()) {
                    if (isHovered((int)mx,(int)my, x+2, iy, PANEL_W-2, SETTING_H)) {
                        scrollSetting(s, amount > 0);
                        return;
                    }
                    iy += SETTING_H;
                }
            }
        }
    }

    // ── Setting interaction ───────────────────────────────────────────────────

    @SuppressWarnings({"unchecked","rawtypes"})
    private void handleSettingClick(Setting<?> s, int button, float mx, float iy) {
        if (s.isBoolean()) {
            ((Setting<Boolean>)s).setValue(!(Boolean)s.getValue());
        } else if (s.isEnum()) {
            s.cycleEnum(button == 1);
        } else if (s.isNumber() && s.getMin() != null && s.getMax() != null) {
            // slider drag – handled in mouseDragged
        }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void scrollSetting(Setting<?> s, boolean up) {
        if (!s.isNumber() || s.getMin() == null || s.getMax() == null) return;
        Object cur = s.getValue();
        if (cur instanceof Integer v) {
            ((Setting<Integer>)s).setValue(v + (up ? 1 : -1));
        } else if (cur instanceof Float v) {
            ((Setting<Float>)s).setValue(v + (up ? 0.1f : -0.1f));
        } else if (cur instanceof Double v) {
            ((Setting<Double>)s).setValue(v + (up ? 0.1 : -0.1));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatValue(Setting<?> s) {
        Object v = s.getValue();
        if (v instanceof Float f) return String.format("%.1f", f);
        if (v instanceof Double d) return String.format("%.1f", d);
        if (v instanceof Boolean b) return b ? "ON" : "OFF";
        return String.valueOf(v);
    }

    private boolean isHovered(int mx, int my,
                               float bx, float by, float bw, float bh) {
        return mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
    }

    private void playClick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getSoundManager().play(
            PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f, 0.5f));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public float    getX()        { return x; }
    public float    getY()        { return y; }
    public Category getCategory() { return category; }
    public boolean  isDragging()  { return dragging; }

    /** Total height of this panel in its current state (for bounds checks). */
    public float getTotalHeight() {
        if (collapsed) return HEADER_H;
        float h = HEADER_H + 1;
        for (Module m : modules) {
            h += MODULE_H;
            if (expandedModule == m) h += m.getSettings().size() * SETTING_H;
        }
        return h;
    }
}
