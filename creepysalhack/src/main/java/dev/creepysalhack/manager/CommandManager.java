package dev.creepysalhack.manager;

import dev.creepysalhack.CreepySalHack;
import dev.creepysalhack.command.Command;
import dev.creepysalhack.command.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandManager {

    public static final String PREFIX = ".";
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        register(new HelpCommand());
        register(new ToggleCommand());
        register(new BindCommand());
        register(new FriendCommand());
        register(new ConfigCommand());
        register(new SetCommand());
    }

    private void register(Command c) { commands.add(c); }

    /** Returns true if the message was a command and should be suppressed. */
    public boolean handle(String message) {
        if (!message.startsWith(PREFIX)) return false;
        String body = message.substring(PREFIX.length()).trim();
        String[] args = body.split("\\s+");
        if (args.length == 0 || args[0].isBlank()) return true;

        Optional<Command> cmd = commands.stream()
                .filter(c -> c.getName().equalsIgnoreCase(args[0]))
                .findFirst();

        if (cmd.isPresent()) {
            cmd.get().execute(args);
        } else {
            CreepySalHack.mc().inGameHud.getChatHud()
                    .addMessage(net.minecraft.text.Text.literal(
                            "§c[CSH] Unknown command. Use .help"));
        }
        return true;
    }

    public List<Command> getCommands() { return commands; }
}
