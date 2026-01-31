package com.voidecho;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class PulseTask extends BukkitRunnable {
    private final VoidEcho plugin;
    private final GameManager gameManager;
    private final Location center;
    private final int size;

    public PulseTask(VoidEcho plugin, GameManager gameManager, Location center, int size) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.center = center;
        this.size = size;
    }

    @Override
    public void run() {
        if (gameManager.getState() != GameState.INGAME) {
            this.cancel();
            return;
        }

        World world = center.getWorld();
        if (world == null) return;
        int half = size / 2;
        int cx = center.getBlockX();
        int cz = center.getBlockZ();

        gameManager.setArenaVisible(true);

        for (UUID uuid : gameManager.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) p.sendTitle("§bECHO PULSE", "§fArena Visible!", 5, 40, 5);
        }

        world.playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 1f, 1f);

        for (int x = cx - half; x <= cx + half; x++) {
            for (int z = cz - half; z <= cz + half; z++) {
                Location l = new Location(world, x, 100, z);
                if (l.getBlock().getType() != Material.WHITE_CONCRETE) {
                    l.getBlock().setType(Material.WHITE_CONCRETE);
                }
            }
        }

        new BukkitRunnable() {
            @Override public void run() {
                if (gameManager.getState() != GameState.INGAME) return;

                gameManager.setArenaVisible(false);

                for (int x = cx - half; x <= cx + half; x++) {
                    for (int z = cz - half; z <= cz + half; z++) {
                        Location l = new Location(world, x, 100, z);
                        if (!gameManager.isSnowballBlock(l)) {
                             l.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 100L);
    }
}