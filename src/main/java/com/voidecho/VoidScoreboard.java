package com.voidecho;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import java.util.*;

public class VoidScoreboard {
    private final VoidEcho plugin;
    private final GameManager gameManager;
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private BukkitTask updateTask;

    public VoidScoreboard(VoidEcho plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void startScoreboard() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameManager.getState() != GameState.INGAME) {
                    stopScoreboard();
                    cancel();
                    return;
                }
                updateAll();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void addPlayer(Player player) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = sb.registerNewObjective("voidecho", Criteria.DUMMY, Component.text("VOID ECHO").color(NamedTextColor.GOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(sb);
        playerScoreboards.put(player.getUniqueId(), sb);
    }

    public void stopScoreboard() {
        if (updateTask != null) updateTask.cancel();
        for (UUID id : playerScoreboards.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        playerScoreboards.clear();
    }

    private void updateAll() {
        for (UUID id : gameManager.getPlayers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) update(p, playerScoreboards.get(id));
        }
    }

    private void update(Player p, Scoreboard sb) {
        if (sb == null) return;
        Objective obj = sb.getObjective("voidecho");
        if (obj == null) return;

        for (String entry : sb.getEntries()) sb.resetScores(entry);

        obj.getScore("§fAlive Players: §a" + gameManager.getPlayers().size()).setScore(1);
        obj.getScore("§7--------------").setScore(0);
    }
}