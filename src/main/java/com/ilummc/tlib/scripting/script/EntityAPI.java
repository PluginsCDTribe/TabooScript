package com.ilummc.tlib.scripting.script;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

/**
 * @Author sky
 * @Since 2018-07-08 20:35
 */
public class EntityAPI {

    private final Plugin plugin;

    public EntityAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public EntityType fromName(String name) {
        return EntityType.fromName(name);
    }

    public EntityType fromId(int id) {
        return EntityType.fromId(id);
    }

    public EntityType type(String name) {
        return EntityType.valueOf(name.toUpperCase());
    }

    public Entity spawn(EntityType type, Location location) {
        return location.getWorld().spawnEntity(location, type);
    }
}
