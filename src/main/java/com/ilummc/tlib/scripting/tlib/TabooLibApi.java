package com.ilummc.tlib.scripting.tlib;

import me.skymc.taboolib.display.ActionUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabooLibApi {

    private final Plugin plugin;

    public TabooLibApi(Plugin plugin) {
        this.plugin = plugin;
    }

    public void actionbar(Player player, String text) {
        ActionUtils.send(player, text);
    }

}
