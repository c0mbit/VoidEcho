package com.voidecho;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VoidListener implements Listener {
    private final GameManager gameManager;
    private final WorldManager worldManager;

    public VoidListener(GameManager gameManager, WorldManager worldManager) {
        this.gameManager = gameManager;
        this.worldManager = worldManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (gameManager.getState() == GameState.LOBBY) {
            gameManager.addPlayer(event.getPlayer());
            worldManager.teleportToVoidWorld(event.getPlayer());
        } else {
            event.getPlayer().sendMessage("§cGame in progress. Please wait.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        gameManager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball snowball && snowball.getShooter() instanceof Player shooter) {
            if (gameManager.isPlayerAlive(shooter.getUniqueId()) && gameManager.getState() == GameState.INGAME) {
                gameManager.createSnowballPlatform(snowball.getLocation());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (gameManager.isPlayerAlive(p.getUniqueId()) && gameManager.getState() == GameState.INGAME) {
            if (p.getLocation().getY() < 85) {
                p.sendMessage("§c[VoidEcho] You fell into the void!");
                gameManager.removePlayer(p);
            }
        }
    }
}