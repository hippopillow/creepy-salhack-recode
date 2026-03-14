package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;

public class ConfigCommand extends Command {
    public ConfigCommand() { super("config", ".config <save|load>", "Save or load config"); }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) { chat("Usage: " + getUsage()); return; }
        switch (args[1].toLowerCase()) {
            case "save" -> { CreepySalHack.getConfigManager().save(); chat("Config saved."); }
            case "load" -> { CreepySalHack.getConfigManager().load(); chat("Config loaded."); }
            default     -> chat("Usage: " + getUsage());
        }
    }
}
