package dev.creepysalhack.command.impl;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;

public class FriendCommand extends Command {
    public FriendCommand() { super("friend", ".friend <add|remove|list> [name]", "Manage friends"); }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) { chat("Usage: " + getUsage()); return; }
        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 3) { chat("Provide a name."); return; }
                CreepySalHack.getFriendManager().addFriend(args[2]);
                chat("Added friend: §b" + args[2]);
            }
            case "remove" -> {
                if (args.length < 3) { chat("Provide a name."); return; }
                CreepySalHack.getFriendManager().removeFriend(args[2]);
                chat("Removed friend: §b" + args[2]);
            }
            case "list" -> CreepySalHack.getFriendManager().getFriends()
                    .forEach(f -> chat("§b" + f));
            default -> chat("Usage: " + getUsage());
        }
    }
}
