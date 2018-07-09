package com.ilummc.tlib.scripting.script;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @Author sky
 * @Since 2018-07-09 9:55
 */
public class SoundAPI {

    private final Plugin plugin;

    public SoundAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, String sound) {
        player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1f, 1f);
    }

    public void play(Player player, String sound, float yaw, float pitch) {
        player.playSound(player.getLocation(), Sound.valueOf(sound.toUpperCase()), yaw, pitch);
    }

    public void play(Location location, String sound) {
        location.getWorld().playSound(location, Sound.valueOf(sound.toUpperCase()), 1f, 1f);
    }

    public void play(Location location, String sound, float yaw, float pitch) {
        location.getWorld().playSound(location, Sound.valueOf(sound.toUpperCase()), yaw, pitch);
    }
}
