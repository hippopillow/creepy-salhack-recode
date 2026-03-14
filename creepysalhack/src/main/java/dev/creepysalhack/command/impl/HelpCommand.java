package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;

public class HelpCommand extends Command {
    public HelpCommand() { super("help", ".help", "Lists all commands"); }

    @Override
    public void execute(String[] args) {
        chat("§7─── Commands ───");
        for (Command c : CreepySalHack.getCommandManager().getCommands()) {
            chat("§b" + c.getUsage() + " §7- " + c.getDescription());
        }
    }
}
