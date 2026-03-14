package dev.creepysalhack;

import dev.creepysalhack.manager.*;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreepySalHack implements ClientModInitializer {

    public static final String NAME    = "Creepy SalHack";
    public static final String VERSION = "1.0.0";
    public static final Logger LOG     = LoggerFactory.getLogger("CreepySalHack");

    // ── Singleton managers ──────────────────────────────────────────────────
    private static EventManager    eventManager;
    private static ModuleManager   moduleManager;
    private static ConfigManager   configManager;
    private static CommandManager  commandManager;
    private static FriendManager   friendManager;
    private static FontManager     fontManager;
    private static HudManager      hudManager;
    private static NotifManager    notifManager;

    @Override
    public void onInitializeClient() {
        LOG.info("[{}] Initialising v{}", NAME, VERSION);

        eventManager   = new EventManager();
        fontManager    = new FontManager();
        friendManager  = new FriendManager();
        moduleManager  = new ModuleManager();
        hudManager     = new HudManager();
        commandManager = new CommandManager();
        configManager  = new ConfigManager();
        notifManager   = new NotifManager();

        configManager.load();

        LOG.info("[{}] {} modules loaded.", NAME, moduleManager.getModules().size());
    }

    // ── Static accessors ────────────────────────────────────────────────────
    public static EventManager   getEventManager()   { return eventManager; }
    public static ModuleManager  getModuleManager()  { return moduleManager; }
    public static ConfigManager  getConfigManager()  { return configManager; }
    public static CommandManager getCommandManager() { return commandManager; }
    public static FriendManager  getFriendManager()  { return friendManager; }
    public static FontManager    getFontManager()    { return fontManager; }
    public static HudManager     getHudManager()     { return hudManager; }
    public static NotifManager   getNotifManager()   { return notifManager; }

    // ── Minecraft shortcut ──────────────────────────────────────────────────
    public static MinecraftClient mc() { return MinecraftClient.getInstance(); }
}
