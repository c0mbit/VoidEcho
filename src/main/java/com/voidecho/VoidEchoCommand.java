package com.voidecho;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class VoidEchoCommand implements CommandExecutor, TabCompleter {
    private final VoidEcho plugin;
    private final GameManager gameManager;
    private final WorldManager worldManager;

    public VoidEchoCommand(VoidEcho plugin, GameManager gameManager, WorldManager worldManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.worldManager = worldManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length == 0) return true;

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "join" -> {
                if (gameManager.getState() == GameState.LOBBY) {
                    gameManager.addPlayer(player);
                    worldManager.teleportToVoidWorld(player);
                } else {
                    player.sendMessage("§cGame in progress.");
                }
            }
            case "arena" -> worldManager.teleportToVoidWorld(player);
            case "setarena" -> {
                if (player.hasPermission("voidecho.admin")) {
                    plugin.saveArenaToConfig(player.getLocation());
                    gameManager.setArenaCenter(player.getLocation());
                    player.sendMessage("§a[VoidEcho] Arena point saved!");
                }
            }
            case "start" -> {
                if (player.hasPermission("voidecho.admin")) gameManager.startGame();
            }
            case "stop" -> {
                if (player.hasPermission("voidecho.admin")) gameManager.forceStop();
            }
            case "leave" -> {
                gameManager.removePlayer(player);
                worldManager.teleportToMainWorld(player);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("join", "arena", "leave", "setarena", "start", "stop");
        return new ArrayList<>();
    }
}