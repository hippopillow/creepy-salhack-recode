package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;
import dev.creepysalhack.module.Module;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class BindCommand extends Command {
    public BindCommand() { super("bind", ".bind <module> <key>", "Binds a module to a key"); }

    @Override
    public void execute(String[] args) {
        if (args.length < 3) { chat("Usage: " + getUsage()); return; }
        Optional<Module> mod = CreepySalHack.getModuleManager().getByName(args[1]);
        if (mod.isEmpty()) { chat("§cModule not found: " + args[1]); return; }
        int key = GLFW.glfwGetKeyScancode(args[2].toUpperCase().charAt(0));
        mod.get().setKeyBind(key);
        CreepySalHack.getConfigManager().save();
        chat("Bound §b" + mod.get().getName() + " §fto §b" + args[2].toUpperCase());
    }
}
