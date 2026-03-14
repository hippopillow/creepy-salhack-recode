package dev.creepysalhack.manager;

import com.google.gson.*;
import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configDir;

    public ConfigManager() {
        configDir = FabricLoader.getInstance().getGameDir().resolve("CreepySalHack");
        try { Files.createDirectories(configDir); }
        catch (IOException e) { CreepySalHack.LOG.error("Could not create config dir", e); }
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    public void save() {
        JsonObject root = new JsonObject();
        for (Module m : CreepySalHack.getModuleManager().getModules()) {
            JsonObject mObj = new JsonObject();
            mObj.addProperty("enabled", m.isEnabled());
            mObj.addProperty("keyBind", m.getKeyBind());

            JsonObject settings = new JsonObject();
            for (Setting<?> s : m.getSettings()) {
                settings.addProperty(s.getName(), String.valueOf(s.getValue()));
            }
            mObj.add("settings", settings);
            root.add(m.getName(), mObj);
        }

        try (Writer w = Files.newBufferedWriter(configDir.resolve("modules.json"))) {
            GSON.toJson(root, w);
        } catch (IOException e) {
            CreepySalHack.LOG.error("Failed to save config", e);
        }
    }

    // ── Load ─────────────────────────────────────────────────────────────────

    public void load() {
        Path file = configDir.resolve("modules.json");
        if (!Files.exists(file)) return;

        try (Reader r = Files.newBufferedReader(file)) {
            JsonObject root = GSON.fromJson(r, JsonObject.class);
            if (root == null) return;

            for (Module m : CreepySalHack.getModuleManager().getModules()) {
                if (!root.has(m.getName())) continue;
                JsonObject mObj = root.getAsJsonObject(m.getName());

                if (mObj.has("enabled") && mObj.get("enabled").getAsBoolean()) {
                    m.setEnabledSilent(true);
                    CreepySalHack.getEventManager().subscribe(m);
                }
                if (mObj.has("keyBind")) m.setKeyBind(mObj.get("keyBind").getAsInt());

                if (mObj.has("settings")) {
                    JsonObject sObj = mObj.getAsJsonObject("settings");
                    for (Setting<?> s : m.getSettings()) {
                        if (!sObj.has(s.getName())) continue;
                        String raw = sObj.get(s.getName()).getAsString();
                        applyRaw(s, raw);
                    }
                }
            }
        } catch (IOException e) {
            CreepySalHack.LOG.error("Failed to load config", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void applyRaw(Setting<T> s, String raw) {
        try {
            T cur = s.getValue();
            if (cur instanceof Boolean) s.setValue((T) Boolean.valueOf(raw));
            else if (cur instanceof Integer) s.setValue((T) Integer.valueOf(raw));
            else if (cur instanceof Double)  s.setValue((T) Double.valueOf(raw));
            else if (cur instanceof Float)   s.setValue((T) Float.valueOf(raw));
            else if (cur instanceof String)  s.setValue((T) raw);
            else if (cur instanceof Enum<?>)  {
                for (Object constant : cur.getClass().getEnumConstants()) {
                    if (((Enum)constant).name().equalsIgnoreCase(raw)) {
                        s.setValue((T) constant);
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
