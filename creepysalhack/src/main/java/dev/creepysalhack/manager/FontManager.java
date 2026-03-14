package dev.creepysalhack.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class FontManager {
    /** Returns the vanilla text renderer – swap with a custom renderer if desired. */
    public TextRenderer getRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }

    public int getStringWidth(String text) {
        return getRenderer().getWidth(text);
    }

    public int getFontHeight() {
        return getRenderer().fontHeight;
    }
}
