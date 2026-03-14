package dev.creepysalhack.manager;

import com.google.gson.*;
import dev.creepysalhack.CreepySalHack;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FriendManager {

    private final Set<String> friends = new HashSet<>();
    private final Path file;

    public FriendManager() {
        file = FabricLoader.getInstance().getGameDir()
                .resolve("CreepySalHack").resolve("friends.json");
        load();
    }

    public void addFriend(String name)    { friends.add(name); save(); }
    public void removeFriend(String name) { friends.remove(name); save(); }
    public boolean isFriend(String name)  { return friends.contains(name); }
    public Set<String> getFriends()       { return Collections.unmodifiableSet(friends); }

    private void save() {
        try (Writer w = Files.newBufferedWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create()
                    .toJson(friends, w);
        } catch (IOException e) { CreepySalHack.LOG.error("Friends save failed", e); }
    }

    private void load() {
        if (!Files.exists(file)) return;
        try (Reader r = Files.newBufferedReader(file)) {
            JsonArray arr = JsonParser.parseReader(r).getAsJsonArray();
            arr.forEach(e -> friends.add(e.getAsString()));
        } catch (Exception e) { CreepySalHack.LOG.error("Friends load failed", e); }
    }
}
