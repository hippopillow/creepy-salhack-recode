package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;
import dev.creepysalhack.module.Module;
import dev.creepysalhack.module.Setting;

import java.util.Optional;

public class SetCommand extends Command {
    public SetCommand() { super("set", ".set <module> <setting> <value>", "Change a module setting"); }

    @Override
    public void execute(String[] args) {
        if (args.length < 4) { chat("Usage: " + getUsage()); return; }
        Optional<Module> mod = CreepySalHack.getModuleManager().getByName(args[1]);
        if (mod.isEmpty()) { chat("§cModule not found: " + args[1]); return; }

        Optional<Setting<?>> setting = mod.get().getSettings().stream()
                .filter(s -> s.getName().equalsIgnoreCase(args[2]))
                .findFirst();

        if (setting.isEmpty()) { chat("§cSetting not found: " + args[2]); return; }

        applySetting(setting.get(), args[3]);
        CreepySalHack.getConfigManager().save();
        chat("Set §b" + args[2] + " §f= §b" + args[3]);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private <T> void applySetting(Setting<T> s, String raw) {
        try {
            T cur = s.getValue();
            if (cur instanceof Boolean)  s.setValue((T) Boolean.valueOf(raw));
            else if (cur instanceof Integer) s.setValue((T) Integer.valueOf(raw));
            else if (cur instanceof Double)  s.setValue((T) Double.valueOf(raw));
            else if (cur instanceof Float)   s.setValue((T) Float.valueOf(raw));
            else if (cur instanceof String)  s.setValue((T) raw);
            else if (cur instanceof Enum<?>) {
                for (Object c : cur.getClass().getEnumConstants()) {
                    if (((Enum)c).name().equalsIgnoreCase(raw)) { s.setValue((T)c); break; }
                }
            }
        } catch (Exception e) { chat("§cInvalid value: " + raw); }
    }
}
