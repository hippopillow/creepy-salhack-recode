package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;
import dev.creepysalhack.module.Module;

import java.util.Optional;

public class ToggleCommand extends Command {
    public ToggleCommand() { super("toggle", ".toggle <module>", "Toggles a module"); }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) { chat("Usage: " + getUsage()); return; }
        Optional<Module> mod = CreepySalHack.getModuleManager().getByName(args[1]);
        if (mod.isEmpty()) { chat("§cModule not found: " + args[1]); return; }
        mod.get().toggle();
        chat(mod.get().getName() + " → " + (mod.get().isEnabled() ? "§aON" : "§cOFF"));
    }
}
