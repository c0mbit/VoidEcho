package com.voidecho;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidEcho extends JavaPlugin {
    private GameManager gameManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        worldManager = new WorldManager(this);
        worldManager.createVoidWorld();

        gameManager = GameManager.getInstance(this, worldManager);

        VoidEchoCommand cmd = new VoidEchoCommand(this, gameManager, worldManager);
        getCommand("voidecho").setExecutor(cmd);

        getServer().getPluginManager().registerEvents(new VoidListener(gameManager, worldManager), this);

        loadArenaFromConfig();
        getLogger().info("VoidEcho enabled!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.forceStop();
    }

    public void saveArenaToConfig(Location loc) {
        getConfig().set("arena.center.x", loc.getX());
        getConfig().set("arena.center.y", loc.getY());
        getConfig().set("arena.center.z", loc.getZ());
        saveConfig();
    }

    public void loadArenaFromConfig() {
        World world = worldManager.getVoidWorld();
        if (world != null) {
            double x = getConfig().getDouble("arena.center.x", 0.5);
            double y = getConfig().getDouble("arena.center.y", 100.0);
            double z = getConfig().getDouble("arena.center.z", 0.5);
            gameManager.setArenaCenter(new Location(world, x, y, z));
        }
    }
}