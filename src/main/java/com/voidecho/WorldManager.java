package com.voidecho;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import java.util.Random;

public class WorldManager {
    private final VoidEcho plugin;
    private World voidWorld;

    public WorldManager(VoidEcho plugin) { this.plugin = plugin; }

    public void createVoidWorld() {
        WorldCreator creator = new WorldCreator("void_echo_world");
        creator.generator(new ChunkGenerator() {
            @Override
            public void generateNoise(@NotNull WorldInfo info, @NotNull Random random, int x, int z, @NotNull ChunkData data) {}
        });
        creator.generateStructures(false);
        voidWorld = creator.createWorld();
        buildLobbyPlatform();
    }

    public void buildLobbyPlatform() {
        if (voidWorld == null) return;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                voidWorld.getBlockAt(x, 100, z).setType(Material.GLASS);
            }
        }
    }

    public void clearLobby() {
        if (voidWorld == null) return;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                voidWorld.getBlockAt(x, 100, z).setType(Material.AIR);
            }
        }
    }

    public void teleportToVoidWorld(Player player) {
        if (voidWorld == null) createVoidWorld();
        player.teleport(new Location(voidWorld, 0.5, 101, 0.5));
        player.sendMessage("§a[VoidEcho] Welcome to the Lobby!");
    }

    public void teleportToMainWorld(Player player) {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    public World getVoidWorld() { return voidWorld; }
}