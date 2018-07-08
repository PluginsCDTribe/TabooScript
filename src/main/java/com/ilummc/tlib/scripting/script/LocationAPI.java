package com.ilummc.tlib.scripting.script;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 * @Author sky
 * @Since 2018-07-08 20:39
 */
public class LocationAPI {

    private final Plugin plugin;

    public LocationAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public Location location(String world, int x, int y, int z) {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public Location location(World world, int x, int y, int z) {
        return new Location(world, x, y, z);
    }

    public Location location(String world, int x, int y, int z, float yaw, float pitch) {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public Location location(World world, int x, int y, int z, float yaw, float pitch) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
