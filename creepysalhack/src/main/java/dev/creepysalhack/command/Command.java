package dev.creepysalhack.command;

public abstract class Command {
    private final String name;
    private final String usage;
    private final String description;

    protected Command(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    public abstract void execute(String[] args);

    public String getName()        { return name; }
    public String getUsage()       { return usage; }
    public String getDescription() { return description; }

    protected void chat(String msg) {
        dev.creepysalhack.CreepySalHack.mc().inGameHud.getChatHud()
                .addMessage(net.minecraft.text.Text.literal("§b[CSH] §f" + msg));
    }
}
