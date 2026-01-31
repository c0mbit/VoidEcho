package com.voidecho;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private static GameManager instance;
    private final VoidEcho plugin;
    private final WorldManager worldManager;
    private GameState state = GameState.LOBBY;
    private final Set<UUID> players = ConcurrentHashMap.newKeySet();
    private Location arenaCenter;
    private final int arenaSize = 60;
    private VoidScoreboard scoreboard;
    private BukkitTask activePulseTask;
    private final Set<Location> activeSnowballBlocks = ConcurrentHashMap.newKeySet();
    private boolean arenaVisible = false;

    private GameManager(VoidEcho plugin, WorldManager worldManager) {
        this.plugin = plugin;
        this.worldManager = worldManager;
    }

    public static GameManager getInstance(VoidEcho plugin, WorldManager worldManager) {
        if (instance == null) instance = new GameManager(plugin, worldManager);
        return instance;
    }

    public void setArenaCenter(Location loc) {
        this.arenaCenter = loc;
    }

    public void addPlayer(Player player) {
        if (state != GameState.LOBBY) {
            player.sendMessage("§cGame already in progress!");
            return;
        }
        players.add(player.getUniqueId());
        Bukkit.broadcastMessage("§6[VoidEcho] §e" + player.getName() + " joined! (" + players.size() + "/12)");
        if (players.size() >= 12) startGame();
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.getInventory().clear();
        for (org.bukkit.potion.PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFallDistance(0);
        player.setVelocity(new org.bukkit.util.Vector());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        worldManager.teleportToMainWorld(player);
        checkWinner();
    }

    public void startGame() {
        if (players.isEmpty()) return;
        state = GameState.INGAME;
        if (arenaCenter == null) arenaCenter = new Location(worldManager.getVoidWorld(), 0.5, 100, 0.5);

        clearArena();
        worldManager.clearLobby();

        scoreboard = new VoidScoreboard(plugin, this);
        scoreboard.startScoreboard();

        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.getInventory().clear();
                giveItems(p);
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(arenaCenter.clone().add(0, 1.5, 0));
                scoreboard.addPlayer(p);
            }
        }

        if (activePulseTask != null) activePulseTask.cancel();
        activePulseTask = new PulseTask(plugin, this, arenaCenter, arenaSize).runTaskTimer(plugin, 0L, 140L);
    }

    public boolean isSnowballBlock(Location loc) {
        return activeSnowballBlocks.contains(loc.toBlockLocation());
    }

    public void clearArena() {
        if (arenaCenter == null) return;
        World world = arenaCenter.getWorld();
        if (world == null) return;

        int half = arenaSize / 2;
        int cx = arenaCenter.getBlockX();
        int cz = arenaCenter.getBlockZ();

        for (int x = cx - half; x <= cx + half; x++) {
            for (int z = cz - half; z <= cz + half; z++) {
                world.getBlockAt(x, 100, z).setType(Material.AIR);
            }
        }
        activeSnowballBlocks.clear();
    }

    public boolean isArenaVisible() {
        return arenaVisible;
    }

    public void setArenaVisible(boolean visible) {
        this.arenaVisible = visible;
    }

    public void createSnowballPlatform(Location impact) {
        if (state != GameState.INGAME) return;

        Location blockLoc = impact.clone();
        blockLoc.setY(100);
        blockLoc = blockLoc.getBlock().getLocation();

        Block block = blockLoc.getBlock();

        if (block.getType() != Material.WHITE_CONCRETE) {
            block.setType(Material.WHITE_CONCRETE);
        }

        activeSnowballBlocks.add(blockLoc);

        final Location finalLoc = blockLoc;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.INGAME) {
                    activeSnowballBlocks.remove(finalLoc);
                    if (!arenaVisible && finalLoc.getBlock().getType() == Material.WHITE_CONCRETE) {
                        finalLoc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }.runTaskLater(plugin, 200L);
    }

    private void giveItems(Player p) {
        p.getInventory().setItem(0, new ItemStack(Material.SNOWBALL, 64));
        ItemStack rod = new ItemStack(Material.FISHING_ROD);
        ItemMeta m = rod.getItemMeta();
        if (m != null) {
            m.addEnchant(Enchantment.KNOCKBACK, 2, true);
            rod.setItemMeta(m);
        }
        p.getInventory().setItem(1, rod);
    }

    public void forceStop() {
        state = GameState.LOBBY;
        if (activePulseTask != null) {
            activePulseTask.cancel();
            activePulseTask = null;
        }
        if (scoreboard != null) scoreboard.stopScoreboard();

        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                worldManager.teleportToMainWorld(p);
                p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        }
        players.clear();
        clearArena();
        worldManager.buildLobbyPlatform();
    }

    private void checkWinner() {
        if (state == GameState.INGAME && players.isEmpty()) {
            forceStop();
            return;
        }
        if (state == GameState.INGAME && players.size() == 1) {
            UUID winnerId = players.iterator().next();
            Player winner = Bukkit.getPlayer(winnerId);
            String name = (winner != null) ? winner.getName() : "Unknown";
            Bukkit.broadcastMessage("§6[VoidEcho] §fGame Over! Winner: §e" + name);
            forceStop();
        }
    }

    public boolean isPlayerAlive(UUID uuid) {
        return players.contains(uuid);
    }

    public GameState getState() {
        return state;
    }

    public Set<UUID> getPlayers() {
        return players;
    }
}

