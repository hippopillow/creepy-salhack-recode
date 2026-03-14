package dev.creepysalhack.gui.click;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.module.Module.Category;
import dev.creepysalhack.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Main ClickGUI screen.
 * Opens with the configured keybind (default: RIGHT_SHIFT).
 * Reproduces the classic Creepy SalHack aesthetic:
 *   - Dark blurred backdrop
 *   - Animated snow particles
 *   - Draggable category panels
 *   - Rainbow accent colours
 *   - Search bar overlay
 */
public class ClickGuiScreen extends Screen {

    // ── Snow ──────────────────────────────────────────────────────────────────
    private static final int SNOW_COUNT = 120;
    private final List<SnowParticle> snow = new ArrayList<>();

    // ── Panels ────────────────────────────────────────────────────────────────
    private final List<CategoryPanel> panels = new ArrayList<>();

    // ── Search ────────────────────────────────────────────────────────────────
    private String searchQuery = "";
    private boolean searchFocused = false;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ClickGuiScreen() {
        super(Text.literal("Creepy SalHack"));
    }

    @Override
    protected void init() {
        snow.clear();
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        for (int i = 0; i < SNOW_COUNT; i++) {
            snow.add(new SnowParticle(sw, sh));
        }

        // Build panels – positions spread across the top
        panels.clear();
        Category[] cats = Category.values();
        float startX = 10;
        float gap    = 118;
        for (int i = 0; i < cats.length; i++) {
            panels.add(new CategoryPanel(cats[i], cats[i].name(), startX + i * gap, 10));
        }
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        // ── Backdrop ────────────────────────────────────────────────────────
        RenderUtil.drawRect(ctx.getMatrices(), 0, 0, sw, sh,
                RenderUtil.toARGB(0, 0, 0, 160));

        // ── Snow particles ──────────────────────────────────────────────────
        for (SnowParticle p : snow) {
            p.tick();
            int snowAlpha = 80 + (int)(p.getSize() * 30);
            RenderUtil.drawRect(ctx.getMatrices(),
                    p.getX(), p.getY(), p.getSize(), p.getSize(),
                    RenderUtil.toARGB(200, 220, 255, snowAlpha));
        }

        // ── Title watermark in centre ────────────────────────────────────
        String title   = "Creepy SalHack";
        String sub     = "v" + CreepySalHack.VERSION + "  |  Right-click panels to collapse";
        var tr         = client.textRenderer;
        int rainbow    = RenderUtil.getTimeRainbow(0.3f, 0.6f, 1.0f, 255);

        ctx.drawTextWithShadow(tr, title,
                sw / 2 - tr.getWidth(title) / 2, 3, rainbow);
        ctx.drawTextWithShadow(tr, sub,
                sw / 2 - tr.getWidth(sub) / 2, 3 + tr.fontHeight + 1, 0xFF666688);

        // ── Panels ────────────────────────────────────────────────────────
        for (CategoryPanel panel : panels) {
            panel.render(ctx, mouseX, mouseY);
        }

        // ── Search bar ────────────────────────────────────────────────────
        renderSearchBar(ctx, sw, sh, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderSearchBar(DrawContext ctx, int sw, int sh, int mx, int my) {
        var tr    = client.textRenderer;
        int barW  = 160;
        int barH  = 16;
        float bx  = sw / 2f - barW / 2f;
        float by  = sh - barH - 8;

        // background
        RenderUtil.drawRoundedRect(ctx.getMatrices(), bx, by, barW, barH, 4,
                RenderUtil.toARGB(20, 20, 30, 220));
        RenderUtil.drawRectOutline(ctx.getMatrices(), bx, by, barW, barH, 1,
                searchFocused
                    ? RenderUtil.getTimeRainbow(0.4f, 0.6f, 1f, 255)
                    : RenderUtil.toARGB(50, 50, 70, 200));

        // prompt or typed text
        String display = searchQuery.isEmpty() ? "§7Search modules..." : searchQuery;
        ctx.drawTextWithShadow(tr, display, (int)(bx + 6), (int)(by + 4), 0xFFCCCCDD);

        // cursor blink
        if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
            int curX = (int)(bx + 6 + tr.getWidth(searchQuery));
            ctx.drawTextWithShadow(tr, "|", curX, (int)(by + 4), 0xFFFFFFFF);
        }

        // filter panels by search
        applySearch();
    }

    private void applySearch() {
        if (searchQuery.isEmpty()) return;
        String q = searchQuery.toLowerCase();
        for (CategoryPanel panel : panels) {
            // panels themselves don't filter here – extend CategoryPanel.render
            // to skip non-matching modules if desired.
        }
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        // search bar click
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        float bx = sw / 2f - 80;
        float by = sh - 24;
        searchFocused = (mx >= bx && mx <= bx+160 && my >= by && my <= by+16);

        if (!searchFocused) {
            for (CategoryPanel panel : panels) {
                if (panel.mouseClicked(mx, my, button)) return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        for (CategoryPanel panel : panels) panel.mouseDragged(mx, my);
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        panels.forEach(CategoryPanel::mouseReleased);
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hScroll, double vScroll) {
        for (CategoryPanel panel : panels) panel.mouseScrolled(mx, my, vScroll);
        return super.mouseScrolled(mx, my, hScroll, vScroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchFocused) {
            if (keyCode == 259 && !searchQuery.isEmpty()) { // BACKSPACE
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                return true;
            }
            if (keyCode == 256) { // ESCAPE – unfocus search first
                searchFocused = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32 && searchQuery.length() < 24) {
            searchQuery += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void close() {
        CreepySalHack.getConfigManager().save();
        super.close();
    }
}
